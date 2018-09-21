package com.pp.toptal.soccermanager.servlet.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

import com.pp.toptal.soccermanager.config.TLConfig;
import com.pp.toptal.soccermanager.controller.tl.TLControllerAbstract;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TLFilter extends GenericFilterBean {
    
    @Autowired
    TLConfig config;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (! process((HttpServletRequest) request, (HttpServletResponse) response)) {
            chain.doFilter(request, response);
        }
    }

    private boolean process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        final String requestPath = getRequestPath(request);
        
        TLControllerAbstract controller = config.resolveController(requestPath);
        if (controller != null) {
            controller.process(requestPath, request, response, getServletContext(), config.getTemplateEngine());
            return true;
        }
        
        return false;
    }
    
    
    private String getRequestPath(final HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        final String contextPath = request.getContextPath();
        
        final int fragmentIndex = requestURI.indexOf(';'); 
        if (fragmentIndex != -1) {
            requestURI = requestURI.substring(0, fragmentIndex);
        }
        
        if (requestURI.startsWith(contextPath)) {
            requestURI = requestURI.substring(contextPath.length());
        }
        
        if (requestURI.endsWith("index.html")) {
            requestURI = requestURI.substring(0, requestURI.lastIndexOf("index"));            
        } else if (requestURI.endsWith(".html")) {
            requestURI = requestURI.substring(0, requestURI.lastIndexOf('.'));
        }

        if (requestURI.endsWith("/")) {
            requestURI = requestURI.substring(0, requestURI.length() - 1);            
        }
        
        return requestURI;
    }

}
