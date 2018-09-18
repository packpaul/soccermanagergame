package com.pp.toptal.soccermanager.config;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.pp.toptal.soccermanager.servlet.filter.ApiAuthHandlingFilter;
import com.pp.toptal.soccermanager.servlet.filter.ApiVersionForwardFilter;
import com.pp.toptal.soccermanager.servlet.filter.TLFilter;

@Configuration
public class WebConfiguration {

    @Bean
    public FilterRegistrationBean getAuthResourceFilterRegistration(
            @Qualifier("authResourceServerFilter") Filter resourceFilter) {

        FilterRegistrationBean registration = new FilterRegistrationBean(resourceFilter);
        registration.setUrlPatterns(Collections.singleton("/auth/api/*"));

        return registration;
    }
    
    @Bean
    public FilterRegistrationBean getProdResourceFilterRegistration(
            @Qualifier("managerResourceServerFilter") Filter resourceFilter) {

        FilterRegistrationBean registration = new FilterRegistrationBean(resourceFilter);
        registration.setUrlPatterns(Collections.singleton("/manager/api/*"));

        return registration;
    }
    
    @Bean("apiAuthHandlingFilterRegistration")
    public FilterRegistrationBean getApiAuthHandlingFilterRegistration(
            ApiAuthHandlingFilter filter, SecurityProperties props) {

        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setOrder(props.getFilterOrder() - 1);
        registration.setUrlPatterns(Collections.singleton("/auth/api/*"));

        return registration;
    }
    
    @Bean("apiVersionForwardFilterRegistration")
    public FilterRegistrationBean getApiVersionForwardFilterRegistration(ApiVersionForwardFilter filter) {

        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 1);
        registration.setUrlPatterns(Arrays.asList(
                "/auth/api/*", "/manager/api/*"));

        return registration;
    }

    @Bean("apiAuthHandlingFilter")
    public ApiAuthHandlingFilter getApiAuthHandlingFilter() {
        return new ApiAuthHandlingFilter();
    }

    @Bean("apiVersionForwardFilter")
    public ApiVersionForwardFilter getApiVersionForwardFilter() {
        return new ApiVersionForwardFilter();
    }
    
    @Bean("tlFilter")
    public TLFilter getTLFilter() {
        return new TLFilter();
    }

}
