package com.pp.toptal.soccermanager.controller.manager.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pp.toptal.soccermanager.so.ApiInfoSO;

@RestController()
@RequestMapping(ManagerApi.ROOT + "/v" + ManagerApi.BASELINE_VERSION + "/info")
public class ManagerInfoController {
    
    @GetMapping
    @PostMapping
    public ApiInfoSO getInfo() {
        ApiInfoSO info = new ApiInfoSO();
        info.setApiRoot(ManagerApi.ROOT);
        info.setApiVersions(ManagerApi.getSupportedVersions());
        
        return info;
    }

}
