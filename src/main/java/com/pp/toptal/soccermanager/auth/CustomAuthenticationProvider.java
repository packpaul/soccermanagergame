package com.pp.toptal.soccermanager.auth;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pp.toptal.soccermanager.entity.UserEntity;
import com.pp.toptal.soccermanager.repo.UserRepo;

public class CustomAuthenticationProvider implements AuthenticationProvider {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepo userRepo;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName().toLowerCase();
        String password = (String) authentication.getCredentials();
        
        UserEntity user = userRepo.findOneByUsername(username);
        if (user == null) {
            throw new BadCredentialsException(String.format("User '%s' not found!", username));
        }
        
        if (! passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException(String.format("Incorrect password for user '%s'!", username));
        }
   
        return new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getUserType().toString())));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

}
