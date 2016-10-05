package com.myretail.api.controller;

import java.math.BigInteger;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.myretail.api.model.Product;
import com.myretail.api.service.ProductService;

@RestController
@RequestMapping("/")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    ProductService productService;

    @RequestMapping(value = { "product/{productId}" }, method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE } )
    public DeferredResult<Product> getProduct( @PathVariable BigInteger productId) {
        
        LOGGER.info( "Input -> productId: {}", productId );
        
        DeferredResult<Product> deffered = new DeferredResult<>();
        
        productService.getProduct(productId)
            .subscribe(
                product -> {
                    deffered.setResult(product);
                    LOGGER.info( "Response -> product: {}", product );
                },
                error -> {
                    deffered.setErrorResult(error);
                    LOGGER.error( "Error Occurred", error );
                } );

        return deffered;
    }

    @RequestMapping(value = { "product/{productId}" }, method = { RequestMethod.PUT },
            consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE } )
    public DeferredResult<Void> putProduct( @PathVariable String productId,
            @Valid @RequestBody Product product) {
        
        LOGGER.info( "Input -> productId: {}", productId );
        
        DeferredResult<Void> deffered = new DeferredResult<>();
        
        productService.putProduct(productId, product)
            .subscribe(
                updated -> {
                    deffered.setResult(null);
                    LOGGER.info( "Response -> product: {}", product );
                },
                error -> {
                    deffered.setErrorResult(error);
                    LOGGER.error( "Error Occurred", error );
                } );

        return deffered;
    }
    
}
