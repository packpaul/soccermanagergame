package com.pp.toptal.soccermanager.controller.manager.api;

import java.util.Collections;
import java.util.List;

/**
 * Manager REST API version holder
 */
public final class ManagerApi {
    
    public static final String ROOT = "/manager/api";

    public static final int V1 = 1;

    public static final int BASELINE_VERSION = V1;
    
    public static final int MINOR_VERSION = 1;
    
    private ManagerApi() {
    }
    
    public static List<String> getSupportedVersions() {
        return Collections.singletonList(String.format("%d.%d", V1, MINOR_VERSION));
    }
    
}
