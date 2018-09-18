package com.pp.toptal.soccermanager.controller.auth.api;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import com.pp.toptal.soccermanager.auth.AuthService;
import com.pp.toptal.soccermanager.so.NullSO;

/**
 * Controller for features related to authentication.
 */
@RestController
@RequestMapping(AuthApi.ROOT)
public class AuthController {
    
    @Autowired
    private TokenEndpoint genericTokenEndpoint;

    @Autowired
    private AuthService authService;

/*
    @GetMapping(path = "/v" + ApiVersion.BASELINE + "/currentUser")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserSO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }
*/
    @PostMapping(path = "/login")
    public ResponseEntity<OAuth2AccessToken> loginBaseline(Principal principal, @RequestParam Map<String, String> parameters)
            throws HttpRequestMethodNotSupportedException {

        return login(principal, parameters);
    }

    @PostMapping(path = "/v" + AuthApi.BASELINE_VERSION + "/login")
    public ResponseEntity<OAuth2AccessToken> login(Principal principal, @RequestParam Map<String, String> parameters)
            throws HttpRequestMethodNotSupportedException {

        return genericTokenEndpoint.postAccessToken(principal, parameters);
    }

    /**
     * Logout method. It's working itself in a secure context, and revokes the tokens of current user.
     */
    @PostMapping(path = "/v" + AuthApi.BASELINE_VERSION + "/logout")
    public NullSO logout() {
        String username = authService.getCurrentUsername();
        authService.revokeTokens(username);
        
        return NullSO.INSTANCE;
    }

}