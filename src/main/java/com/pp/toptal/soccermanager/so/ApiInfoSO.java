package com.pp.toptal.soccermanager.so;

import java.io.Serializable;
import java.util.List;

public class ApiInfoSO implements Serializable {

    private static final long serialVersionUID = -8251053590020013075L;

    private String apiRoot;
    private List<String> apiVersions;

    public String getApiRoot() {
        return apiRoot;
    }
    public void setApiRoot(String apiRoot) {
        this.apiRoot = apiRoot;
    }

    public List<String> getApiVersions() {
        return apiVersions;
    }
    public void setApiVersions(List<String> apiVersions) {
        this.apiVersions = apiVersions;
    }
    
}
