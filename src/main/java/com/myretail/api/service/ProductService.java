package com.myretail.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myretail.api.dao.ProductFieldsDAO;
import com.myretail.api.model.Price;
import com.myretail.api.model.Product;
import com.myretail.api.model.ProductAttributes;
import com.myretail.core.datastore.DataAccessObject;
import com.myretail.core.ipc.IPCClient;
import com.myretail.core.resilience.CircuitCommand;

import rx.Observable;

@Service("productService")
public class ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private IPCClient productFieldsIPCClient;
    
    @Autowired
    private DataAccessObject<Price> priceDAO;
    
    public Observable<Product> getProduct(final String productId) {
        
        return Observable.zip(
                new CircuitCommand<ProductAttributes>( "GetProductDescription",
                        () -> productFieldsIPCClient.getForObject("/product/"+productId+"/fields?ids=description", ProductAttributes.class)
                    ).observe()
                    .map(productAttributes -> productAttributes.getAttributes().get("description")),
                new CircuitCommand<Price>( "GetPrice",
                        () -> priceDAO.getItemById(productId)
                    ).observe(),
                (name, price) -> {
                    Product product = new Product();
                    product.setId(productId);
                    product.setName(name);
                    product.setCurrentPrice(price);
                    return product;
                } );
    }

    public Observable<Boolean> putProduct(Product product) {
        return new CircuitCommand<Boolean>("PutPrice", () -> priceDAO.addItemAtId(product.getName(), product.getCurrentPrice())).observe();
    }
}
