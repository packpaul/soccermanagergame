package com.pp.toptal.soccermanager;

/**
 * Application version holder.
 */
public final class AppVersion {
    
    private AppVersion() {
    }
    
    public static String getVersion() {
        return AppVersion.class.getPackage().getSpecificationVersion();
    }
    
    public static String getFullVersion() {
        return AppVersion.class.getPackage().getImplementationVersion();
    }
    
}
