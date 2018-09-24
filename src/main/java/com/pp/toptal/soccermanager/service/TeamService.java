package com.pp.toptal.soccermanager.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pp.toptal.soccermanager.entity.PlayerEntity;
import com.pp.toptal.soccermanager.entity.PlayerEntity.PlayerType;
import com.pp.toptal.soccermanager.entity.TeamEntity;
import com.pp.toptal.soccermanager.entity.UserEntity;
import com.pp.toptal.soccermanager.entity.UserType;
import com.pp.toptal.soccermanager.exception.BusinessException;
import com.pp.toptal.soccermanager.exception.ErrorCode;
import com.pp.toptal.soccermanager.repo.PlayerRepo;
import com.pp.toptal.soccermanager.repo.TeamRepo;

@Service
public class TeamService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);
    
    private static final int GENERATION_REATTEMPTS = 4;
    
    @Autowired
    private TeamRepo teamRepo;

    @Autowired
    private PlayerRepo playerRepo;
    
    @Autowired
    private EntityManager em;
    
    @Autowired
    private TeamGenerator teamGenerator;

    @Autowired
    private PlayerGenerator playerGenerator;

    @Transactional
    public TeamEntity generateInitialTeam(UserEntity user) {
        
        if (user.getUserType() == UserType.ADMIN) {
            LOGGER.warn("Initial team generation for administrators is impossible.");
            return null;
        }
        
        TeamEntity generatedTeam = null;
        for (int i = 1; i <= GENERATION_REATTEMPTS; i++) {
            TeamEntity team = teamGenerator.generate();
            if (teamRepo.findOneByTeamnameAndCountry(team.getTeamname(), team.getCountry()) == null) {
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
            if (playerRepo.findOneByFirstnameAndLastnameAndCountry(
                    player.getFirstname(), player.getLastname(), player.getCountry()) == null) {
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
 
}
