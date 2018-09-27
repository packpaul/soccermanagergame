package com.pp.toptal.soccermanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import com.pp.toptal.soccermanager.entity.QPlayerEntity;
import com.pp.toptal.soccermanager.entity.TeamEntity;
import com.pp.toptal.soccermanager.entity.UserType;
import com.pp.toptal.soccermanager.exception.BusinessException;
import com.pp.toptal.soccermanager.exception.DataParameterException;
import com.pp.toptal.soccermanager.exception.ErrorCode;
import com.pp.toptal.soccermanager.mapper.EntityToSOMapper;
import com.pp.toptal.soccermanager.repo.OffsetBasedPageRequest;
import com.pp.toptal.soccermanager.repo.PlayerRepo;
import com.pp.toptal.soccermanager.repo.TeamRepo;
import com.pp.toptal.soccermanager.so.TableDataSO;
import com.pp.toptal.soccermanager.so.PlayerSO;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

@Service
public class PlayerService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerService.class);
    
    @Autowired
    private PlayerRepo playerRepo;
    
    @Autowired
    private TeamRepo teamRepo;
    
    @Autowired
    private EntityToSOMapper toSoMapper;
    
    @Autowired
    private AuthService authService;

    public List<PlayerSO> getPlayers(SelectionParameters params) {
        return getPlayersInternal(params).getValue0();
    }
    
    public Triplet<List<PlayerSO>, Long, Long> getPlayersInternal(SelectionParameters params) {
        
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
            QPlayerEntity entity = QPlayerEntity.playerEntity;
            for (int i = 0; i < properties.length; i++) {
                Predicate propPredicate;
                if (Objects.equals(properties[i], entity.firstName.getMetadata().getName())) {
                    String value = values[i];
                    if (! value.matches("[\\w\\* ]+")) {
                        throw new DataParameterException("Filter for 'firstName' should only contain [a-zA-Z_0-9* ] !");
                    }
                    value = value.replace('*', '%');
                    propPredicate = entity.firstName.likeIgnoreCase(value);
                } else if (Objects.equals(properties[i], entity.lastName.getMetadata().getName())) {
                    String value = values[i];
                    if (! value.matches("[\\w\\* ]+")) {
                        throw new DataParameterException("Filter for 'lastName' should only contain [a-zA-Z_0-9* ] !");
                    }
                    value = value.replace('*', '%');
                    propPredicate = entity.lastName.likeIgnoreCase(value);
                } else if (Objects.equals(properties[i], entity.country.getMetadata().getName())) {
                    Country value = Country.valueOf(values[i]);
                    propPredicate = entity.country.eq(value);
                } else if (Objects.equals(properties[i], entity.playerType.getMetadata().getName())) {
                    PlayerType value = PlayerType.valueOf(values[i]);
                    propPredicate = entity.playerType.eq(value);
                } else if ("teamId".equals(properties[i])) { // TODO: move to property name builder from metadata
                    propPredicate = entity.team.id.eq(Long.valueOf(values[i]));
                } else {
                    continue;
                }
                
                predicate = (predicate != null) ? ExpressionUtils.and(predicate, propPredicate) :
                                                  propPredicate;
            }
        }
        
        final long count = playerRepo.count(predicate);
        
        List<PlayerSO> players = new ArrayList<>(Math.min(limit, (int) count));

        Iterable<PlayerEntity> pi = playerRepo.findAll(predicate, pageReq);
        for (PlayerEntity p : pi) {
            players.add(toSoMapper.map(p, new PlayerSO()));
        }
        
        if (predicate != null) {
            return Triplet.with(players, null, count);
        }
        
        return Triplet.with(players, count, count);
    }

    public TableDataSO<PlayerSO> listPlayers(SelectionParameters params) {
        
        Triplet<List<PlayerSO>, Long, Long> playersData = getPlayersInternal(params);
        
        TableDataSO<PlayerSO> result = new TableDataSO<>();
        result.setData(playersData.getValue0());
        result.setCountTotal(Optional.ofNullable(
                playersData.getValue1()).orElseGet(() -> playerRepo.count()));
        result.setCountFiltered(playersData.getValue2());

        return result;
    }

    public PlayerSO getPlayer(Long playerId) {
        return toSoMapper.map(findPlayer(playerId), new PlayerSO());
    }
    
    private PlayerEntity findPlayer(Long playerId) {
        PlayerEntity player = playerRepo.findOne(playerId);
        if (player == null) {
            throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                    String.format("Player (id=%d) not found!", playerId));
        }
        
        return player;
    }
    
    @Transactional
    public PlayerSO addPlayer(PlayerSO playerData) {
        
        if (authService.isCurrentUserType(UserType.TEAM_OWNER)) {
            throw new DataParameterException("Team owners cannot add new players!");
        }
        
        if (StringUtils.isBlank(playerData.getFirstName())) {
            throw new DataParameterException("Player first name should not be blank!");
        }
        
        if (playerData.getPlayerType() == null) {
            throw new DataParameterException("Player type should be provided!");
        }
        
        String lastName = StringUtils.isNotBlank(playerData.getLastName()) ? playerData.getLastName() : null;
        
        if (playerData.getCountry() == null) {
            throw new DataParameterException("Player country should be provided!");
        }
        
        if ((playerData.getAge() == null) || (! PlayerGenerator.isAgeValid(playerData.getAge()))) {
            throw new DataParameterException("Player's age is not valid!");
        }
        
        if (playerData.getCountry() == null) {
            throw new DataParameterException("Player country should be provided!");
        }
        
        TeamEntity team = teamRepo.findOne(playerData.getTeamId());
        if (team == null) {
            throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND, "Player's team should be provided!");
        }
        
        if ((playerData.getValue() == null) || (playerData.getValue() < 0)) {
            throw new DataParameterException("Player's value should be provided and non negative");
        }

        PlayerEntity player = playerRepo.save(new PlayerEntity(
                playerData.getFirstName().trim(),
                lastName,
                PlayerType.valueOf(playerData.getPlayerType()),
                playerData.getAge(),
                Country.valueOf(playerData.getCountry()),
                team,
                playerData.getValue()));
        
        LOGGER.info(String.format("New player (id=%d) was added.", player.getId()));
        
        return toSoMapper.map(player, new PlayerSO());
    }

    @Transactional
    public PlayerSO updatePlayer(Long playerId, PlayerSO playerData) {
        
        if (authService.isCurrentUserType(UserType.TEAM_OWNER)) {
            throw new DataParameterException("Team owners cannot update players!");
        }
        
        PlayerEntity player = findPlayer(playerId);
        
        String firstName = Optional.ofNullable(playerData.getFirstName())
                .map(String::trim).filter((tn) -> (! Objects.equals(tn, player.getFirstName()))).orElse(null);
        String lastName = Optional.ofNullable(playerData.getLastName())
                .map(String::trim).filter((tn) -> (! Objects.equals(tn, player.getLastName()))).orElse(null);
        Integer age = (! Objects.equals(playerData.getAge(), player.getAge())) ? playerData.getAge() : null;
        PlayerType playerType = Optional.ofNullable(playerData.getPlayerType())
                .map(PlayerType::valueOf).filter((t) -> (t != player.getPlayerType())).orElse(null);
        Long value = (! Objects.equals(playerData.getValue(), player.getValue())) ? playerData.getValue() : null;
        Country country = Optional.ofNullable(playerData.getCountry())
                .map(Country::valueOf).filter((c) -> (c != player.getCountry())).orElse(null);
        Long teamId = (! Objects.equals(playerData.getTeamId(), player.getTeam().getId())) ? playerData.getTeamId() : null; 
        
        boolean isChanged = false;
        
        if (firstName != null) {
            player.setFirstName(firstName);
            isChanged = true;
        }
        if (lastName != null) {
            if (lastName.isEmpty()) {
                lastName = null;
            }
            if (! Objects.equals(lastName, player.getLastName())) {
                player.setLastName(lastName);
                isChanged = true;
            }
        }
        if (age != null) {
            if (! PlayerGenerator.isAgeValid(age)) {
                throw new DataParameterException("Player's age is not valid!");
            }
            player.setAge(age);
            isChanged = true;
        }
        if (playerType != null) {
            player.setPlayerType(playerType);
            isChanged = true;
        }
        if (value != null) {
            if (value < 0) {
                throw new DataParameterException("Player's value should not be negative!");
            }
            player.setValue(value);
            isChanged = true;
        }
        if (country != null) {
            player.setCountry(country);
            isChanged = true;
        }
        if (teamId != null) {
            TeamEntity team = teamRepo.findOne(teamId);
            if (player.isInTransfer()) {
                throw new BusinessException(ErrorCode.INVALID_STATE,
                        String.format("Player (id=%d) is in transfer. Team cannot be changed!", teamId));
            }
            if (team == null) {
                throw new BusinessException(ErrorCode.OBJECT_NOT_FOUND,
                        String.format("Team (id=%d) not found!", teamId));
            }
            player.setTeam(team);
            isChanged = true;
        }

        if (isChanged) {
            player.setUpdateDate(new Date());
        }
        
        LOGGER.info(String.format("Player (id=%d) was updated.", playerId));
        
        return toSoMapper.map(playerRepo.save(player), new PlayerSO());
    }

    public void deletePlayer(Long playerId) {
        findPlayer(playerId);
        
        playerRepo.delete(playerId);
    }
 
}
