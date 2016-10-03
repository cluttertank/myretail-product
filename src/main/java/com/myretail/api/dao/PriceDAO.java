package com.myretail.api.dao;

import java.math.BigDecimal;

import org.springframework.stereotype.Repository;

import com.myretail.api.model.Price;
import com.myretail.api.model.Product;
import com.myretail.core.exception.BadRequestException;

@Repository("priceDAO")
public class PriceDAO {

    public Price getCurrentPrice(String productId) {
        throw new BadRequestException("Bad, very bad");
//        return new Price(new BigDecimal(100.01), "USD");
    }

    public Boolean putCurrentPrice(Product product) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }
    
}
