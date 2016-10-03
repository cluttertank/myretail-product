package com.myretail.api.model;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Price {
    
    private BigDecimal value;
    
    @JsonProperty("currency_code")
    private String currencyCode;

    public Price() {}
    
    public Price(BigDecimal value, String currencyCode) {
        this.value = value;
        this.currencyCode = currencyCode;
    }
    
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}