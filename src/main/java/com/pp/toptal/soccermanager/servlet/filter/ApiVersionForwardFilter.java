package com.pp.toptal.soccermanager.servlet.filter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

import com.pp.toptal.soccermanager.controller.auth.api.AuthApi;
import com.pp.toptal.soccermanager.controller.manager.api.ManagerApi;

public class ApiVersionForwardFilter extends GenericFilterBean {
    
    private final Pattern apiPattern = Pattern.compile("^(/\\w+/api)(/.+)");
    private final Pattern apiVerPattern = Pattern.compile("^/\\w+/api/v\\d+/.+");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        String uri = request.getRequestURI();
        
        String forwardUri = getForwardURI(uri);
        if (forwardUri != null) {
            new HttpServletRequestWrapper(request)
                .getRequestDispatcher(forwardUri).forward(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    String getForwardURI(String uri) {
        
        if (apiVerPattern.matcher(uri).matches()) {
            return null;
        }
        
        Matcher m = apiPattern.matcher(uri);
        if (! m.matches()) {
            return null;
        }
        
        final String apiRoot = m.group(1);
        
        final int apiVersion =
                ManagerApi.ROOT.equals(apiRoot) ? ManagerApi.BASELINE_VERSION :
                AuthApi.ROOT.equals(apiRoot) ? AuthApi.BASELINE_VERSION :
                0;
        if (apiVersion == 0) {
            return null;
        }

        final String method = m.group(2);
        
        return apiRoot + "/v" + apiVersion + method;
    }

}
