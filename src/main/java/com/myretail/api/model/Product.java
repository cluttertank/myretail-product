package com.myretail.api.model;

import java.math.BigInteger;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {
    
    @Min(0)
    @NotNull
    private BigInteger id;
    
    private String name;
    
    @Valid
    @NotNull
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
