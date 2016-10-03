package com.myretail.api.model;

import java.math.BigInteger;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {
    
    private BigInteger id;
    
    private String name;
    
    @JsonProperty("current_price")
    private Price currentPrice;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
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
