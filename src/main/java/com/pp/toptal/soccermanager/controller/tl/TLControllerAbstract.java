package com.pp.toptal.soccermanager.controller.tl;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.pp.toptal.soccermanager.config.TLConfig;

public abstract class TLControllerAbstract {
    
    private static final String PAGE_ROOT_VAR_NAME = "pageRoot";
    
    public void process(String templateName,
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext, final TemplateEngine templateEngine) throws IOException {
       
        response.setContentType("text/html;charset=UTF-8");

        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma", "no-cache");
        
        WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        fillContext(ctx);
        
        String resolvedTemplateName = resolveTemplateName(templateName, ctx);
        setContextVariable(PAGE_ROOT_VAR_NAME, TLConfig.PAGES_ROOT + resolvedTemplateName, ctx);
        
        templateEngine.process(resolvedTemplateName, ctx, response.getWriter()); 
    }
    
    protected abstract void fillContext(WebContext ctx);
    
    String resolveTemplateName(String templateName, WebContext ctx) {
        return templateName;
    }
    
    void setContextVariable(String name, final Object value, WebContext ctx) {
        if (ctx.getVariables().containsKey(name)) {
            throw new IllegalArgumentException(String.format(
                    "Variable '%s' is already defined in the context!", name));
        }
        ctx.setVariable(name, value);
    }
    
}
