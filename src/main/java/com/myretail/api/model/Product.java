package com.myretail.api.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Product {
    
    private String id;
    
    private String name;
    
    private Price currentPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Price getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Price currentPrice) {
        this.currentPrice = currentPrice;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
