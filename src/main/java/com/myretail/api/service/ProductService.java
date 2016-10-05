package com.myretail.api.service;

import java.math.BigInteger;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myretail.api.model.Price;
import com.myretail.api.model.Product;
import com.myretail.api.model.ProductAttributes;
import com.myretail.core.datastore.DataAccessObject;
import com.myretail.core.exception.InternalServerErrorException;
import com.myretail.core.exception.ResourceNotFoundException;
import com.myretail.core.ipc.IPCClient;
import com.myretail.core.ipc.IPCClientException;
import com.myretail.core.resilience.CircuitCommand;

import rx.Observable;

@Service("productService")
public class ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private IPCClient productFieldsIPCClient;
    
    @Autowired
    private DataAccessObject priceDAO;
    
    public Observable<Product> getProduct(final BigInteger productId) {
        
        return Observable.zip(
                getProductAttributesCommand(productId).observe()
                    .onErrorReturn( error -> ipcErrorHandler(error) )
                    .map(productAttributes -> getDescription(productAttributes) ),
                getPriceCommand(productId).observe()
                    .onErrorReturn( error -> handlePriceDataError(error) ),
                (description, price) -> composeProduct(productId, description, price) );
    }

    private Product composeProduct(BigInteger productId, String description, Price price) {
        LOGGER.info("Product description = {}", description);
        LOGGER.info("Product price = {}", price);
        if( description == null && price == null ) {
            throw new ResourceNotFoundException("Product price and description not found");
        }
        Product product = new Product();
        product.setId(productId);
        product.setName(description);
        product.setCurrentPrice(price);
        return product;
    }

    private CircuitCommand<ProductAttributes> getProductAttributesCommand(BigInteger productId) {
        return new CircuitCommand<ProductAttributes>( "GetProductDescription",
                () -> productFieldsIPCClient.getForObject("/product/"+productId+"/fields?ids=description", ProductAttributes.class)
            );
    }

    private String getDescription(ProductAttributes productAttributes) {

        String description = null;
        Map<String, String> attributes = null;
        LOGGER.info("Product attributes = {}", productAttributes);
        if( productAttributes != null && (attributes = productAttributes.getAttributes()) != null ) { 
            description = attributes.getOrDefault("description", "");
        }
        return description;
    }

    private ProductAttributes ipcErrorHandler(Throwable error) {

        Throwable cause = null;
        if( (cause = error.getCause()) != null && cause instanceof IPCClientException ) {
            IPCClientException ipcException = (IPCClientException)cause;
            LOGGER.error("IPCClient Exception occured: {} - {}", ipcException.getHttpStatusCode(), ipcException.getMessage());
            if( ipcException.getHttpStatusCode() == 404 ) {
                return null;
            }
        }
        
        throw new InternalServerErrorException("Error occurred during ipc call for product details", error);
    }

    private CircuitCommand<Price> getPriceCommand(BigInteger productId) {
        return new CircuitCommand<Price>( "GetPrice",
                () -> priceDAO.getItemById(productId.toString(), Price.class)
            );
    }

    private Price handlePriceDataError(Throwable error) {
        throw new InternalServerErrorException("Error occurred during price fetch from DataStore", error);
    }

    public Observable<Boolean> putProduct(String productId, Product product) {
        return new CircuitCommand<Boolean>("PutPrice", () ->
                priceDAO.addItemAtId(productId, product.getCurrentPrice())).observe()
                .onErrorReturn( error -> { throw new InternalServerErrorException("Error while adding price to DataStore", error); } );
    }
}
