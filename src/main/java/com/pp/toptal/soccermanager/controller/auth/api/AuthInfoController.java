package com.pp.toptal.soccermanager.controller.auth.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pp.toptal.soccermanager.so.ApiInfoSO;

@RestController()
@RequestMapping(AuthApi.ROOT + "/v" + AuthApi.BASELINE_VERSION + "/info")
public class AuthInfoController {
    
    @GetMapping
    @PostMapping
    public ApiInfoSO getInfo() {
        ApiInfoSO info = new ApiInfoSO();
        info.setApiRoot(AuthApi.ROOT);
        info.setApiVersions(AuthApi.getSupportedVersions());
        
        return info;
    }

}
