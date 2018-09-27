package com.pp.toptal.soccermanager.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.pp.toptal.soccermanager.entity.MessageEntity;
import com.pp.toptal.soccermanager.entity.PlayerEntity;
import com.pp.toptal.soccermanager.entity.ProposalEntity;
import com.pp.toptal.soccermanager.entity.TeamEntity;
import com.pp.toptal.soccermanager.entity.TransferEntity;
import com.pp.toptal.soccermanager.entity.UserEntity;
import com.pp.toptal.soccermanager.so.MessageSO;
import com.pp.toptal.soccermanager.so.PlayerSO;
import com.pp.toptal.soccermanager.so.ProposalSO;
import com.pp.toptal.soccermanager.so.TeamSO;
import com.pp.toptal.soccermanager.so.TransferSO;
import com.pp.toptal.soccermanager.so.UserSO;

@Component
public class EntityToSOMapper {

    public UserSO map(final UserEntity from, UserSO to) {
        to.setId(from.getId());
        to.setUsername(from.getUsername());
        to.setUserType(from.getUserType().name());
        to.setCreationDate(from.getCreationDate());
        to.setUpdateDate(from.getUpdateDate());

        return to;
    }
    
    public TeamSO map(final TeamEntity from, TeamSO to) {
        to.setId(from.getId());
        to.setTeamName(from.getTeamName());
        to.setCountry(from.getCountry().name());
        to.setValue(from.getValue());
        to.setBalance(from.getBalance());
        to.setOwner(from.getOwner().getUsername());
        to.setCreationDate(from.getCreationDate());
        to.setUpdateDate(from.getUpdateDate());

        return to;
    }
    
    public PlayerSO map(final PlayerEntity from, PlayerSO to) {
        to.setId(from.getId());
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setAge(from.getAge());
        to.setPlayerType(from.getPlayerType().name());
        to.setValue(from.getValue());
        to.setCountry(from.getCountry().name());
        to.setTeamId(from.getTeam().getId());
        to.setTeamName(from.getTeam().getTeamName());
        to.setTeamCountry(from.getTeam().getCountry().name());
        to.setInTransfer(from.isInTransfer());

        to.setCreationDate(from.getCreationDate());
        to.setUpdateDate(from.getUpdateDate());

        return to;
    }
    
    public TransferSO map(final TransferEntity from, TransferSO to) {
        
        to.setId(from.getId());
        Optional.ofNullable(from.getPlayer()).ifPresent((p) -> {
            to.setPlayerId(p.getId());
            to.setPlayerFullName(p.getFullName());
            to.setPlayerType(p.getPlayerType().name());
            to.setPlayerAge(p.getAge());
            to.setPlayerValue(p.getValue());
            to.setPlayerCountry(p.getCountry().name());
        });
        Optional.ofNullable(from.getFromTeam()).ifPresent((t) -> {
            to.setFromTeamId(t.getId());
            to.setFromTeamName(t.getTeamName());
            to.setFromTeamCountry(t.getCountry().name());
            Optional.ofNullable(t.getOwner()).ifPresent((o) -> {
                to.setFromTeamOwnerUsername(o.getUsername());                
            });
        });
        Optional.ofNullable(from.getToTeam()).ifPresent((t) -> {
            to.setToTeamId(t.getId());
            to.setToTeamName(t.getTeamName());
            to.setToTeamCountry(t.getCountry().name());
        });
        to.setPrice(from.getPrice());
        
        to.setCreationDate(from.getCreationDate());
        to.setUpdateDate(from.getUpdateDate());
        
        return to;
    }
    
    public ProposalSO map(final ProposalEntity from, ProposalSO to) {

        to.setId(from.getId());
        Optional.ofNullable(from.getTransfer()).ifPresent((t) -> {
            to.setTransferId(t.getId());
            Optional.ofNullable(t.getPlayer()).ifPresent((p) -> {
                to.setPlayerId(p.getId());
                to.setPlayerFullName(p.getFullName());
                to.setPlayerType(p.getPlayerType().name());
                to.setPlayerAge(p.getAge());
                to.setPlayerCountry(p.getCountry().name());
                to.setPlayerValue(p.getValue());
            });
        }); 
        Optional.ofNullable(from.getToTeam()).ifPresent((t) -> {
            to.setToTeamId(t.getId());
            to.setToTeamName(t.getTeamName());
            to.setToTeamCountry(t.getCountry().name());
            Optional.ofNullable(t.getOwner()).ifPresent((o) -> {
                to.setToTeamOwnerUsername(o.getUsername());                
            });
        });
        to.setPrice(from.getPrice());
        
        to.setCreationDate(from.getCreationDate());
        to.setUpdateDate(from.getUpdateDate());
        
        return to;
    }
    
    public MessageSO map(final MessageEntity from, MessageSO to) {
        to.setId(from.getId());
        Optional.ofNullable(from.getFromUser()).ifPresent((u) -> {
            to.setFromUserId(u.getId());
            to.setFromUsername(u.getUsername());
        }); 
        Optional.ofNullable(from.getToUser()).ifPresent((u) -> {
            to.setToUserId(u.getId());
            to.setToUsername(u.getUsername());
        });
        Optional.ofNullable(from.getPrevMessage()).ifPresent((m) -> {
            to.setPrevMessageId(m.getId());
        });
        Optional.ofNullable(from.getNextMessage()).ifPresent((m) -> {
            to.setNextMessageId(m.getId());
        });
        to.setContent(from.getContent());
        to.setRead(from.isRead());
        
        return to;
    }

}
