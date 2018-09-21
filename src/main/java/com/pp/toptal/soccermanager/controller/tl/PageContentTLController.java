package com.pp.toptal.soccermanager.controller.tl;

import org.springframework.stereotype.Controller;
import org.thymeleaf.context.WebContext;

/**
 * This is a controller to test TL in work.
 */
@Controller
public class PageContentTLController extends TLControllerAbstract {
    
    public PageContentTLController() {
        super(false);
    }
    
    @Override
    String resolveTemplateName(String templateName) {
        return "/_content";
    }

    @Override
    protected void fillContext(WebContext ctx) {
    }

}
