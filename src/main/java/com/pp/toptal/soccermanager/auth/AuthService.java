package com.pp.toptal.soccermanager.auth;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import com.pp.toptal.soccermanager.config.properties.AuthProperties;
import com.pp.toptal.soccermanager.entity.UserType;
import com.pp.toptal.soccermanager.exception.BusinessException;
import com.pp.toptal.soccermanager.exception.ErrorCode;
import com.pp.toptal.soccermanager.repo.UserRepo;
import com.pp.toptal.soccermanager.service.UserService;

@Service
public class AuthService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    
    public static final String GRANT_TYPE_PARAM = "grant_type";
    public static final String USERNAME_PARAM = "username";
    public static final String PASSWORD_PARAM = "password";
    
    @Autowired
    private ConsumerTokenServices tokenServices;
    
    @Autowired
    private AuthProperties authProperties;
    
    @Autowired
    private TokenStore tokenStore;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private UserService userService;
    
    public String getCurrentUsername() {
        if (getContext().getAuthentication() == null) {
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
    
    public UserType getCurrentUserType() {
        if (getContext().getAuthentication() == null) {
            return null;
        }
        Collection<? extends GrantedAuthority> authorities =
                getContext().getAuthentication().getAuthorities();
        if (authorities.isEmpty()) {
            return null;
        }
        
        String authority = authorities.iterator().next().getAuthority();
        
        return UserType.valueOf(authority);
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
    
    /**
     * Registers a new user to the system as a team owner if there's no user with the same username.
     * 
     * @param username
     * @param password
     * 
     * @throws BusinessException
     */
    public void register(String username, String password) throws BusinessException {
        
        username = username.trim().toLowerCase();
        
        if (username.isEmpty()) {
            throw new BusinessException(ErrorCode.MISSING_CREDENTIALS,
                    String.format("Username may not be empty or blank!", username));
        }
        
        if (userRepo.findOneByUsername(username) != null) {
            throw new BusinessException(ErrorCode.DUPLICATE_CREDENTIALS,
                    String.format("Username '%s%' already in use!", username));
        }
        
        userService.createUser(username, UserType.TEAM_OWNER, passwordEncoder.encode(password));
    }
    
}
