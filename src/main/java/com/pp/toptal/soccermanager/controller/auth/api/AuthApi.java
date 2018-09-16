package com.pp.toptal.soccermanager.controller.auth.api;

import java.util.Collections;
import java.util.List;

/**
 * Auth REST API version holder
 */
public final class AuthApi {
    
    public static final String ROOT = "/auth/api";

    public static final int V1 = 1;

    public static final int BASELINE_VERSION = V1;
    
    public static final int MINOR_VERSION = 1;
    
    private AuthApi() {
    }
    
    public static List<String> getSupportedVersions() {
        return Collections.singletonList(String.format("%d.%d", V1, MINOR_VERSION));
    }
    
}
