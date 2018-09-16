package com.pp.toptal.soccermanager.controller;
import org.springframework.stereotype.Controller;
import org.thymeleaf.context.WebContext;

/**
 * This is a controller to test TL in work.
 */
@Controller
public class TestTLController extends TLController {
    
    @Override
    protected void fillContext(WebContext ctx) {
        ctx.setVariable("online", "Online");
    }

}
