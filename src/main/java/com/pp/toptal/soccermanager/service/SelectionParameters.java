package com.pp.toptal.soccermanager.service;

import java.util.Objects;

public class SelectionParameters {
    
    private String[] filterProperties;
    private String[] filterValues;
    
    private String orderProperty;
    private String orderDir;
    
    private Long offset;
    private Long limit;
    
    public SelectionParameters filters(String[] filterProperties, String[] filterValues) {
        Objects.requireNonNull(filterProperties);
        Objects.requireNonNull(filterValues);
        
        if (filterProperties.length != filterValues.length) {
            throw new IllegalArgumentException("Filter properties and values sizes should equal!");
        }
        
        this.filterProperties = filterProperties;
        this.filterValues = filterValues;
        
        return this;
    }
    
    public String[] getFilterProperties() {
        return filterProperties;
    }

    public String[] getFilterValues() {
        return filterValues;
    }

    /**
     * @param orderProperty
     * @param orderDir  asc|desc
     * @return
     */
    public SelectionParameters order(String orderProperty, String orderDir) {
        Objects.requireNonNull(orderProperty);
        Objects.requireNonNull(orderDir);
        
        this.orderProperty = orderProperty;
        this.orderDir = orderDir;
        
        return this;        
    }
    
    public String getOrderProperty() {
        return orderProperty;
    }

    public String getOrderDir() {
        return orderDir;
    }

    public SelectionParameters offset(Long offset) {
        this.offset = offset;
        
        return this;
    }

    public SelectionParameters limit(Long limit) {
        this.limit = limit;
        
        return this;
    }

    public Long getOffset() {
        return offset;
    }

    public Long getLimit() {
        return limit;
    }
        
}
