package com.pp.toptal.soccermanager.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthProperties {
    
    @Value("${oauth2.manager.client.id}")
    private String oauth2ManagerClientId;

    public String getOauth2ManagerClientId() {
        return oauth2ManagerClientId;
    }

}
