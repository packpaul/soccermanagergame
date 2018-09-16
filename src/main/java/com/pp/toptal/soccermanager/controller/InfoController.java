package com.pp.toptal.soccermanager.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pp.toptal.soccermanager.AppVersion;
import com.pp.toptal.soccermanager.controller.auth.api.AuthInfoController;
import com.pp.toptal.soccermanager.controller.manager.api.ManagerInfoController;
import com.pp.toptal.soccermanager.so.AppInfoSO;

@RestController()
@RequestMapping("/info")
public class InfoController {
    
    @Autowired
    private AuthInfoController authInfoController;

    @Autowired
    private ManagerInfoController managerInfoController;

    @GetMapping
    public AppInfoSO getAppInfo() {
        
        AppInfoSO info = new AppInfoSO();
        info.setVersion(AppVersion.getVersion());
        info.setFullVersion(AppVersion.getFullVersion());
        info.setApiInfos(Arrays.asList(
                authInfoController.getInfo(),
                managerInfoController.getInfo()));
        
        return info;
    }
    
}
