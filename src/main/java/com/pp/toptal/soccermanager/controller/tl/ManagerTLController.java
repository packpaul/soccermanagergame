package com.pp.toptal.soccermanager.controller.tl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.thymeleaf.context.WebContext;

import com.pp.toptal.soccermanager.auth.AuthService;
import com.pp.toptal.soccermanager.config.TLConfig;
import com.pp.toptal.soccermanager.repo.UserRepo;

/**
 * TL Controller of main page
 */
@Controller
public class ManagerTLController extends TLControllerAbstract {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserRepo userRepo;

    @Override
    String getTemplateName(String requestPath) {
        return "/_manager";
    }

    @Override
    protected void fillContext(String requestPath, WebContext ctx) {
        setContextVariable(PAGE_ROOT_VAR_NAME, TLConfig.PAGES_ROOT + getTemplateName(requestPath), ctx);
        setContextVariable("username", authService.getCurrentUsername(), ctx);
        setContextVariable("userType", authService.getCurrentUserType().name(), ctx);
        setContextVariable("userId",
                userRepo.findOneByUsername(authService.getCurrentUsername()).getId(), ctx);

    }
    
}
