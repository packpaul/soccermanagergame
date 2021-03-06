package com.pp.toptal.soccermanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pp.toptal.soccermanager.auth.AuthService;
import com.pp.toptal.soccermanager.entity.Country;
import com.pp.toptal.soccermanager.entity.PlayerEntity;
import com.pp.toptal.soccermanager.entity.PlayerType;
import com.pp.toptal.soccermanager.entity.QTeamEntity;
import com.pp.toptal.soccermanager.entity.TeamEntity;
import com.pp.toptal.soccermanager.entity.UserEntity;
import com.pp.toptal.soccermanager.entity.UserType;
import com.pp.toptal.soccermanager.exception.BusinessException;
import com.pp.toptal.soccermanager.exception.DataParameterException;
import com.pp.toptal.soccermanager.exception.ErrorCode;
import com.pp.toptal.soccermanager.mapper.EntityToSOMapper;
import com.pp.toptal.soccermanager.repo.OffsetBasedPageRequest;
import com.pp.toptal.soccermanager.repo.PlayerRepo;
import com.pp.toptal.soccermanager.repo.TeamRepo;
import com.pp.toptal.soccermanager.repo.UserRepo;
import com.pp.toptal.soccermanager.so.TableDataSO;
import com.pp.toptal.soccermanager.so.TeamSO;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

@Service
public class TeamService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);
    
    private static final int GENERATION_REATTEMPTS = 4;
    
    @Autowired
    private TeamRepo teamRepo;

    @Autowired
    private PlayerRepo playerRepo;
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private EntityManager em;
    
    @Autowired
    private TeamGenerator teamGenerator;

    @Autowired
    private PlayerGenerator playerGenerator;
    
    @Autowired
    private EntityToSOMapper toSoMapper;
    
    @Autowired
    private AuthService authService;

    @Transactional
    public TeamEntity generateInitialTeam(UserEntity user) {
        
        if (user.getUserType() == UserType.ADMIN) {
            LOGGER.warn("Initial team generation for administrators is impossible.");
            return null;
        }
        
        TeamEntity generatedTeam = null;
        for (int i = 1; i <= GENERATION_REATTEMPTS; i++) {
            TeamEntity team = teamGenerator.generate();
            if (teamRepo.findOneByTeamNameAndCountry(team.getTeamName(), team.getCountry()) == null) {
                generatedTeam = team;
                break;
            }
        }
        if (generatedTeam == null) {
            throw new BusinessException(ErrorCode.INVALID_STATE, "Initial team generation failed!");
        }
        
        generatedTeam.setBalance(5_000_000L);
        generatedTeam.setOwner(user);

        generatedTeam = teamRepo.save(generatedTeam);
        
        generateInitialPlayers(generatedTeam);
        
        em.refresh(generatedTeam);
        
        return generatedTeam;
    }

    private void generateInitialPlayers(TeamEntity team) {
        
        List<PlayerEntity> generatedPlayers = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            generatedPlayers.add(generateInitialPlayer(PlayerType.GOALKEEPER, team));
        }
        for (int i = 1; i <= 6; i++) {
            generatedPlayers.add(generateInitialPlayer(PlayerType.DEFENDER, team));
        }
        for (int i = 1; i <= 6; i++) {
            generatedPlayers.add(generateInitialPlayer(PlayerType.MIDFIELDER, team));
        }
        for (int i = 1; i <= 5; i++) {
            generatedPlayers.add(generateInitialPlayer(PlayerType.ATTACKER, team));
        }
        
        playerRepo.save(generatedPlayers);
    }

    private PlayerEntity generateInitialPlayer(PlayerType playerType, TeamEntity team) {
       
        PlayerEntity generatedPlayer = null;
        for (int i = 1; i <= GENERATION_REATTEMPTS; i++) {
            PlayerEntity player = playerGenerator.generate();
            if (playerRepo.findOneByFirstNameAndLastNameAndCountry(
                    player.getFirstName(), player.getLastName(), player.getCountry()) == null) {
                generatedPlayer = player;
                break;
            }
        }
        if (generatedPlayer == null) {
            throw new BusinessException(ErrorCode.INVALID_STATE, "Initial player generation failed!");
        }
        
        generatedPlayer.setPlayerType(playerType);
        generatedPlayer.setValue(1_000_000L);
        generatedPlayer.setTeam(team);
        
        return generatedPlayer;
    }
    
    public List<TeamSO> getTeams(SelectionParameters params) {
        return getTeamsInternal(params).getValue0();
    }
    
    public Triplet<List<TeamSO>, Long, Long> getTeamsInternal(SelectionParameters params) {
        
        Sort sort = null;
        if (params.getOrderProperty() != null) {
            sort = new Sort(Sort.Direction.fromString(params.getOrderDir()), params.getOrderProperty());
        }
        
        final int limit = (params.getLimit() != null) ? params.getLimit().intValue() : Integer.MAX_VALUE; 
        
        OffsetBasedPageRequest pageReq = new OffsetBasedPageRequest(
                (params.getOffset() != null) ? params.getOffset().intValue() : 0,
                limit,
                sort);
        
        Predicate predicate = null;

        if (params.getFilterProperties() != null) {
            String[] properties = params.getFilterProperties();
            String[] values = params.getFilterValues();
            QTeamEntity entity = QTeamEntity.teamEntity;
            for (int i = 0; i < properties.length; i++) {
                Predicate propPredicate;
                if (Objects.equals(properties[i], entity.teamName.getMetadata().getName())) {
                    String value = values[i];
                    if (! value.matches("[\\w\\* ]+")) {
                        throw new DataParameterException("Filter for 'teamName' should only contain [a-zA-Z_0-9* ] !");
                    }
                    value = value.replace('*', '%');
                    propPredicate = entity.teamName.likeIgnoreCase(value);
                } else if (Objects.equals(properties[i], entity.id.getMetadata().getName())) {
                    propPredicate = entity.id.eq(Long.valueOf(values[i]));
                } else if (Objects.equals(properties[i], entity.country.getMetadata().getName())) {
                    Country value = Country.valueOf(values[i]);
                    propPredicate = entity.country.eq(value);
                } else if (TeamSO.OWNER_ID_PROP_NAME.equals(properties[i])) {
                    propPredicate = entity.owner.id.eq(Long.valueOf(values[i]));
                } else {
                    continue;
                }
                
                predicate = (predicate != null) ? ExpressionUtils.and(predicate, propPredicate) :
                                                  propPredicate;
            }
        }
        
        final long count = teamRepo.count(predicate);
        
        List<TeamSO> teams = new ArrayList<>(Math.min(limit, (int) count));

        Iterable<TeamEntity> ti = teamRepo.findAll(predicate, pageReq);
        for (TeamEntity t : ti) {
            teams.add(toSoMapper.map(t, new TeamSO()));
        }
        
        if (predicate != null) {
            return Triplet.with(teams, null, count);
        }
        
        return Triplet.with(teams, count, count);
    }

    public TableDataSO<TeamSO> listTeams(SelectionParameters params) {
        
        Triplet<List<TeamSO>, Long, Long> teamsData = getTeamsInternal(params);
        
        TableDataSO<TeamSO> result = new TableDataSO<>();
        result.setData(teamsData.getValue0());
        result.setCountTotal(Optional.ofNullable(
                teamsData.getValue1()).orElseGet(() -> teamRepo.count()));
        result.setCountFiltered(teamsData.getValue2());

        return result;
    }

    public TeamSO getTeam(Long teamId) {
        return toSoMapper.map(findTeam(teamId), new TeamSO());
    }
    
    private TeamEntity findTeam(Long teamId) {
        TeamEntity team = teamRepo.findOne(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                    String.format("Team (id=%d) not found!", teamId));
        }
        
        return team;
    }
    
    @Transactional
    public TeamSO addTeam(TeamSO teamData) {
        
        if (authService.isCurrentUserType(UserType.TEAM_OWNER)) {
            throw new DataParameterException("Team owners cannot add new teams!");
        }
        
        if (StringUtils.isBlank(teamData.getTeamName())) {
            throw new DataParameterException("Team name should be not blank!");
        }
        
        if (teamData.getCountry() == null) {
            throw new DataParameterException("Team country should be provided!");
        }
            
        if (teamData.getOwnerId() == null) {
            throw new DataParameterException("Team owner should be provided!");            
        }
        UserEntity owner = userRepo.findOne(teamData.getOwnerId());
        if (owner == null) {
            throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                    String.format("User (id=%d) not found!", teamData.getOwnerId()));
        }
        if (owner.getUserType() == UserType.ADMIN) {
            throw new DataParameterException(
                    String.format("Administrator (username='%s') cannot have teams!", owner.getUsername()));
        }
        
        if ((teamData.getBalance() == null) || (teamData.getBalance() < 0)) {
            throw new DataParameterException("Team balance should be provided and not negative!");
        }
        
        TeamEntity team = teamRepo.save(new TeamEntity(
                teamData.getTeamName().trim(), Country.valueOf(
                teamData.getCountry()),
                owner,
                teamData.getBalance()));
        
        LOGGER.info(String.format("New team (id=%d) was added.", team.getId()));
        
        return toSoMapper.map(team, new TeamSO());
    }

    @Transactional
    public TeamSO updateTeam(Long teamId, TeamSO teamData) {
        
        if (authService.isCurrentUserType(UserType.TEAM_OWNER)) {
            throw new DataParameterException("Team owners cannot update teams!");
        }
        
        TeamEntity team = findTeam(teamId);
        
        String teamName = Optional.ofNullable(teamData.getTeamName())
                .map(String::trim).filter((tn) -> (! Objects.equals(tn, team.getTeamName()))).orElse(null);
        Country country = Optional.ofNullable(teamData.getCountry())
                .map(Country::valueOf).filter((c) -> (c != team.getCountry())).orElse(null);
        Long ownerId = (! Objects.equals(teamData.getOwnerId(), team.getOwner().getId())) ? teamData.getOwnerId() : null;
        Long balance = (! Objects.equals(teamData.getBalance(), team.getBalance())) ? teamData.getBalance() : null;
        
        boolean isChanged = false;
        
        if (teamName != null) {
            team.setTeamName(teamName);
            isChanged = true;
        }
        if (country != null) {
            team.setCountry(country);
            isChanged = true;
        }
        if (ownerId != null) {
            UserEntity owner = userRepo.findOne(ownerId);
            if (owner == null) {
                throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                        String.format("User (id=%d) not found!", ownerId));
            }
            if (owner.getUserType() == UserType.ADMIN) {
                throw new DataParameterException(
                        String.format("Administrator (username='%s') cannot have teams!", owner.getUsername()));
            }
            team.setOwner(owner);
            isChanged = true;
        }
        if (balance != null) {
            if (balance < 0) {
                throw new DataParameterException("Balance should not be negative!");
            }
            team.setBalance(balance);
            isChanged = true;
        }

        if (isChanged) {
            team.setUpdateDate(new Date());
        }
        
        LOGGER.info(String.format("Team (id=%d) was updated.", teamId));
        
        return toSoMapper.map(teamRepo.save(team), new TeamSO());
    }

    public void deleteTeam(Long teamId) {
        if (! authService.isCurrentUserType(UserType.ADMIN)) {
            throw new DataParameterException("Only administrator can delete teams!");
        }
        
        findTeam(teamId);
        
        teamRepo.delete(teamId);
    }

}
