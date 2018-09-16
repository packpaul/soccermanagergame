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

public class ApiAuthHandlingFilter extends GenericFilterBean {
    
    private final String API_AUTH = "/auth/api/login";
    private final Pattern apiVerAuthPattern = Pattern.compile("^/auth/api/v\\d+/login$");
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        if (! API_AUTH.equals(request.getRequestURI())) {
            Matcher m = apiVerAuthPattern.matcher(request.getRequestURI());
            if (! m.matches()) {
                chain.doFilter(request, response);
                return;
            }
        }

        // TODO: A hack to make 'clientCredentialsTokenEndpointFilter' get involved.
        //       There must be a better way to do it.
        request = new HttpServletRequestWrapper(request) {
            @Override
            public String getRequestURI() {
                return "/oauth/token";
            }
        };
        
        chain.doFilter(request, response);
        response.setHeader("WWW-Authenticate","");
    }
    
}
