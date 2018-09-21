package com.pp.toptal.soccermanager.controller.tl;

import org.springframework.stereotype.Controller;
import org.thymeleaf.context.WebContext;

/**
 * TL Controller of main page
 */
@Controller
public class ManagerTLController extends TLControllerAbstract {

    @Override
    String resolveTemplateName(String templateName, WebContext ctx) {
        return "/_manager";
    }

    @Override
    protected void fillContext(WebContext ctx) {
    }
    
}
