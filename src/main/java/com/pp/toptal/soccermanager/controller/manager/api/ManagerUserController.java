package com.pp.toptal.soccermanager.controller.manager.api;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pp.toptal.soccermanager.auth.AuthService;
import com.pp.toptal.soccermanager.service.SelectionParameters;
import com.pp.toptal.soccermanager.service.UserService;
import com.pp.toptal.soccermanager.so.TableDataSO;
import com.pp.toptal.soccermanager.so.UserSO;

@RestController()
@RequestMapping(ManagerApi.ROOT + "/v" + ManagerApi.BASELINE_VERSION + "/user")
public class ManagerUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;
    
    @GetMapping
    public List<UserSO> getUsers(
            @RequestParam(name = "filterProperties[]", required = false) String[] filterProperties,
            @RequestParam(name = "filterValues[]", required = false) String[] filterValues,
            @RequestParam(name = "orderProperty", required = false) String orderProperty,
            @RequestParam(name = "orderDir", required = false) String orderDir,
            @RequestParam(name = "offset", required = false) Long offset,
            @RequestParam(name = "limit", required = false) Long limit) {

        SelectionParameters params = new SelectionParameters();
        if ((filterProperties != null) && (filterValues != null)) {
            params.filters(filterProperties, filterValues);            
        }
        if ((orderProperty != null) && (orderDir != null)) {
            params.order(orderProperty, orderDir);            
        }
        params.offset(offset).limit(limit);
        
        return userService.getUsers(params);
    }
    
    @GetMapping("/list")
    public TableDataSO<UserSO> listUsers(
            @RequestParam(name = "filterProperties[]", required = false) String[] filterProperties,
            @RequestParam(name = "filterValues[]", required = false) String[] filterValues,
            @RequestParam(name = "orderProperty", required = false) String orderProperty,
            @RequestParam(name = "orderDir", required = false) String orderDir,
            @RequestParam(name = "offset", required = false) Long offset,
            @RequestParam(name = "limit", required = false) Long limit) {
        
        SelectionParameters params = new SelectionParameters();
        if ((filterProperties != null) && (filterValues != null)) {
            params.filters(filterProperties, filterValues);            
        }
        if ((orderProperty != null) && (orderDir != null)) {
            params.order(orderProperty, orderDir);            
        }
        params.offset(offset).limit(limit);
        
        return userService.listUsers(params);
    }

    @GetMapping("/{userId}")
    public UserSO getUser(@PathVariable("userId") Long userId) {
          
        return userService.getUser(userId);
    }
    
    @PutMapping("/{userId}")
    public UserSO updateUser(
            @PathVariable("userId") Long userId, @RequestBody UserSO userData) {
        
        final Date now = new Date();
        final String oldUsername = authService.getCurrentUsername();
          
        UserSO user = userService.updateUser(userId, userData);
        if (now.compareTo(user.getUpdateDate()) <= 0) {
            if (! oldUsername.equals(user.getUsername())) {
                authService.revokeTokens(user.getUsername());
            }
        }
        
        return user;
    }

}