package com.pp.toptal.soccermanager.so;

import java.io.Serializable;
import java.util.List;

public class AppInfoSO implements Serializable {

    private static final long serialVersionUID = 7207985426699703946L;

    private String version;
    private String fullVersion;
    private List<ApiInfoSO> apiInfos;
    
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public String getFullVersion() {
        return fullVersion;
    }
    public void setFullVersion(String fullVersion) {
        this.fullVersion = fullVersion;
    }
    
    public List<ApiInfoSO> getApiInfos() {
        return apiInfos;
    }
    public void setApiInfos(List<ApiInfoSO> apiInfos) {
        this.apiInfos = apiInfos;
    }
    
}