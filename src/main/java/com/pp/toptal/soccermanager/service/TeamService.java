package com.pp.toptal.soccermanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                } else if (Objects.equals(properties[i], entity.country.getMetadata().getName())) {
                    Country value = Country.valueOf(values[i]);
                    propPredicate = entity.country.eq(value);
                } else if (Objects.equals(properties[i], entity.owner.getMetadata().getName())) {
                    String value = values[i];
                    if (! value.matches("[\\w\\*]+")) {
                        throw new DataParameterException("Filter for 'owner' should only contain [a-zA-Z_0-9*] !");
                    }
                    value = value.replace('*', '%');
                    propPredicate = entity.owner.username.likeIgnoreCase(value);
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
    public TeamSO updateTeam(Long teamId, TeamSO teamData) {
        
        TeamEntity team = findTeam(teamId);
        
        String teamName = Optional.ofNullable(teamData.getTeamName())
                .map(String::trim).filter((tn) -> (! Objects.equals(tn, team.getTeamName()))).orElse(null);
        Country country = Optional.ofNullable(teamData.getCountry())
                .map(Country::valueOf).filter((c) -> (c != team.getCountry())).orElse(null);
        String owner = Optional.ofNullable(teamData.getOwner())
                .filter((o) -> (! Objects.equals(o, team.getOwner().getUsername()))).orElse(null);
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
        if (owner != null) {
            UserEntity ownerEntity = userRepo.findOneByUsername(owner);
            if (ownerEntity == null) {
                throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                        String.format("User (username='%s') not found!", owner));
            }
            team.setOwner(ownerEntity);
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
        findTeam(teamId);
        
        teamRepo.delete(teamId);
    } 
 
}
