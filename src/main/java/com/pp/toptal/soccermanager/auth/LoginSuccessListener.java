package com.pp.toptal.soccermanager.auth;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

import com.pp.toptal.soccermanager.entity.TeamEntity;
import com.pp.toptal.soccermanager.entity.UserEntity;
import com.pp.toptal.soccermanager.repo.UserRepo;
import com.pp.toptal.soccermanager.service.TeamService;

public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent>{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginSuccessListener.class);

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private TeamService teamService;

    @Override
    public synchronized void onApplicationEvent(AuthenticationSuccessEvent event) {

        if (event.getAuthentication().getPrincipal().getClass() != String.class) {
            // PP: it's a hack, consider using information from
            //     org.springframework.security.core.userdetails.User
            return;
        }
        
        final String username = event.getAuthentication().getName();
        UserEntity user = userRepo.findOneByUsername(username);
        if ((user == null) || (user.getLastLoginDate() != null)) {
            return;
        }
        user.setLastLoginDate(new Date());
        
        TeamEntity team = teamService.generateInitialTeam(user);
        if (team == null) {
            return;
        }
        
        LOGGER.info(String.format("User '%s' is logging-in for the first time, " +
                                  "initial team (id=%d) is generated for him/her...",
                                  username, team.getId()));
    }

}