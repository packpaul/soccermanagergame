package com.pp.toptal.soccermanager.auth;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import com.pp.toptal.soccermanager.config.properties.AuthProperties;

@Service
public class AuthService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private ConsumerTokenServices tokenServices;
    
    @Autowired
    private AuthProperties authProperties;
    
    @Autowired
    private TokenStore tokenStore;
    
    public String getCurrentUsername() {
        if(getContext().getAuthentication() == null) {
            return null;
        }
        Object principal = getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
    
    private SecurityContext getContext() {
        return SecurityContextHolder.getContext();
    }
    
    public void revokeTokens(String username) {
        Collection<OAuth2AccessToken> tokens =
                tokenStore.findTokensByClientIdAndUserName(authProperties.getOauth2ManagerClientId(), username);
        for (OAuth2AccessToken token : tokens) {
            if (tokenServices.revokeToken(token.getValue())) {
                LOGGER.info("Token {} of user {} was revoked.", token.getValue(), username);
            } else {
                LOGGER.debug("Unable to revoke tokens for user {}.", username);
            }
        }
    }
    
}
