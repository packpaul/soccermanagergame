package com.pp.toptal.soccermanager.mapper;

import org.springframework.stereotype.Component;

import com.pp.toptal.soccermanager.entity.TeamEntity;
import com.pp.toptal.soccermanager.entity.UserEntity;
import com.pp.toptal.soccermanager.so.TeamSO;
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

}
