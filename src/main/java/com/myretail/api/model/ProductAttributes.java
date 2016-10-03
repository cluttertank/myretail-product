package com.myretail.api.model;

import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ProductAttributes {
    
    private String id;
    
    private Map<String, String> attributes;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
