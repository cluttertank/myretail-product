package com.myretail.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.myretail.api.model.Price;
import com.myretail.core.config.PropertyManager;
import com.myretail.core.datastore.DataAccessObject;
import com.myretail.core.datastore.DataStore;
import com.myretail.core.datastore.MapDataStore;
import com.myretail.core.datastore.MappedObjectDAO;
import com.myretail.core.ipc.IPCClient;
import com.myretail.core.ipc.RestTemplateIPCClient;

@Configuration
public class SpringConfiguration {

    @Bean
    public DataStore dataStore() {
        return new MapDataStore("data.json");
    }
    
    @Bean
    @Autowired
    public DataAccessObject<Price> priceDAO(DataStore dataStore) {
        return new MappedObjectDAO<>("price", dataStore);
    }
    
    @Bean
    public IPCClient productFieldsIPCClient() {
        return new RestTemplateIPCClient("product-fields")
                .header("Content-Type", "application/json")
                .baseUrl(PropertyManager.getProperty("restTemplate.baseUrl.product-fields", "http://localhost:8081"));
    }
}
