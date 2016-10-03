package com.myretail.api.dao;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Repository;

@Repository("productFieldsDAO")
public class ProductFieldsDAO {
    
    public String getProductDescription(String productId) {
        return getProductFields(Arrays.asList("Description")).getOrDefault("Description", "");
    }

    public Map<String, String> getProductFields(List<String> fields) {
        
        return Collections.unmodifiableMap(Stream.of(
                new SimpleEntry<>("Description", "Random product"),
                new SimpleEntry<>("Department", "Others"))
                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
    }

}
