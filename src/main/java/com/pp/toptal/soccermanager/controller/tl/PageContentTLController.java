package com.pp.toptal.soccermanager.controller.tl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.thymeleaf.context.WebContext;

import com.pp.toptal.soccermanager.auth.AuthService;
import com.pp.toptal.soccermanager.config.TLConfig;

/**
 * This is a controller to test TL in work.
 */
@Controller
public class PageContentTLController extends TLControllerAbstract {

    private static final String PAGE_VAR_NAME = "page";
    
    @Autowired
    private AuthService authService;
    
    @Override
    String getTemplateName(String requestPath) {
        return "/_content";
    }

    @Override
    protected void fillContext(String requestPath, WebContext ctx) {
        final String pageName = getPageName(requestPath);
        setContextVariable(PAGE_ROOT_VAR_NAME, TLConfig.PAGES_ROOT + pageName, ctx);
        setContextVariable(PAGE_VAR_NAME, pageName, ctx);
        setContextVariable("username", authService.getCurrentUsername(), ctx);
        setContextVariable("userType", authService.getCurrentUserType().name(), ctx);
    }
    
    private String getPageName(String requestPath) {
        int idx = requestPath.lastIndexOf('/');
        return (idx >= 0) ? requestPath.substring(idx) : ("/" + requestPath);
    }

}
