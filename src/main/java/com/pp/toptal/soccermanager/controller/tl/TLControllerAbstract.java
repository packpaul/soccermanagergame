package com.pp.toptal.soccermanager.controller.tl;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

public abstract class TLControllerAbstract {
    
    static final String PAGE_ROOT_VAR_NAME = "pageRoot";
    
    public final void process(String requestPath,
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext, final TemplateEngine templateEngine) throws IOException {
       
        response.setContentType("text/html;charset=UTF-8");

        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma", "no-cache");
        
        WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        fillContext(requestPath, ctx);
        
        final String templateName = getTemplateName(requestPath);
        
        templateEngine.process(templateName, ctx, response.getWriter());
    }
    
    protected abstract void fillContext(String requestPath, WebContext ctx);
    
    String getTemplateName(String requestPath) {
        return requestPath;
    }
    
    final void setContextVariable(String name, final Object value, WebContext ctx) {
        if (ctx.getVariables().containsKey(name)) {
            throw new IllegalArgumentException(String.format(
                    "Variable '%s' is already defined in the context!", name));
        }
        ctx.setVariable(name, value);
    }

}
