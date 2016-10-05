package com.myretail.api;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static com.jayway.restassured.module.mockmvc.config.AsyncConfig.withTimeout;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import com.myretail.api.model.Price;
import com.myretail.api.model.Product;
import com.myretail.api.model.ProductAttributes;
import com.myretail.api.service.ProductService;
import com.myretail.core.app.AppSpringConfiguration;
import com.myretail.core.datastore.DataAccessObject;
import com.myretail.core.ipc.IPCClient;
import com.myretail.core.ipc.IPCClientException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppSpringConfiguration.class)
@WebAppConfiguration
public class ProductControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Mock
    private IPCClient productFieldsIPCClient;

    @Mock
    private DataAccessObject<Price> priceDAO;

    @Autowired
    @InjectMocks
    private ProductService productService;

    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);

        RestAssuredMockMvc.webAppContextSetup(context);
        RestAssuredMockMvc.config().asyncConfig(withTimeout(10, TimeUnit.SECONDS));
        setupData();
    }

    @After
    public void tearDown() {
        RestAssuredMockMvc.reset();
    }

    @Test
    public void testGetProductRequest() {
        given()
            .accept(ContentType.JSON)
        .when().async().timeout(5000)
            .get("/product/12345678")
         .then().apply(print())
             .contentType(ContentType.JSON)
             .statusCode(200)
             .body("id", equalTo(12345678))
             .body("name", equalTo("Twin Peaks CD"))
             .body("current_price.value", equalTo(49.01F))
             .body("current_price.currency_code", equalTo("USD"));
    }

    @Test
    public void testIncorrectURI() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/product/1/1")
        .then().apply(print())
            .statusCode(404);
    }

    @Test
    public void testStringProductId() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/product/xyz")
        .then().apply(print())
            .contentType(ContentType.JSON)
            .statusCode(400);
    }

    @Test
    public void testDataStoreError() {
        given()
            .accept(ContentType.JSON)
        .when().async().timeout(5000)
            .get("/product/321")
         .then().apply(print())
             .contentType(ContentType.JSON)
             .statusCode(500)
             .body("message", equalTo("Error occurred during price fetch from DataStore"));
    }

    @Test
    public void testIPCClientError() {
        given()
            .accept(ContentType.JSON)
        .when().async().timeout(5000)
            .get("/product/432")
         .then().apply(print())
             .contentType(ContentType.JSON)
             .statusCode(500)
             .body("message", equalTo("Error occurred during ipc call for product details"));
    }

    @Test
    public void testResourceNotFoundError() {
        given()
            .accept(ContentType.JSON)
        .when().async().timeout(5000)
            .get("/product/543")
         .then().apply(print())
             .contentType(ContentType.JSON)
             .statusCode(404)
             .body("message", equalTo("Product price and description not found"));
    }

    @Test
    public void testPutProductRequest() {
        Product product = new Product();
        product.setId(new BigInteger("87654321"));
        Price price = new Price(new BigDecimal(9.99), "USD");
        product.setName("Prod1");
        product.setCurrentPrice(price);
        
        given()
            .contentType(ContentType.JSON)
            .body(product)
        .when().async().timeout(5000)
            .put("/product/87654321")
         .then().apply(print())
             .statusCode(200);
    }

    @Test
    public void testPut_BadPrice() {
        Product product = new Product();
        product.setId(new BigInteger("123"));
        Price price = new Price(new BigDecimal(-0.01), "USD");
        product.setName("Prod1");
        product.setCurrentPrice(price);
        
        given()
            .contentType(ContentType.JSON)
            .body(product)
        .when()
            .put("/product/87654321")
         .then().apply(print())
             .statusCode(400);
    }

    @Test
    public void testPut_NullPrice() {
        Product product = new Product();
        product.setId(new BigInteger("123"));
        product.setName("Prod1");
        
        given()
            .contentType(ContentType.JSON)
            .body(product)
        .when()
            .put("/product/87654321")
         .then().apply(print())
             .statusCode(400);
    }

    @Test
    public void testPut_DatastoreError() {
        Product product = new Product();
        product.setId(new BigInteger("765"));
        Price price = new Price(new BigDecimal(0.01), "USD");
        product.setName("Prod1");
        product.setCurrentPrice(price);
        
        given()
            .contentType(ContentType.JSON)
            .body(product)
        .when().async().timeout(5000)
            .put("/product/765")
         .then().apply(print())
             .statusCode(500)
             .body("message", equalTo("Error while adding price to DataStore"));
    }

    public void setupData() {

        ProductAttributes productAttributes = new ProductAttributes();
        productAttributes.setId("12345678");
        productAttributes.setAttributes(Collections.unmodifiableMap(new HashMap<String, String>() {{
            put("description", "Twin Peaks CD");
        }}));

        Price price = new Price(new BigDecimal(49.01), "USD");
        
        Mockito
            .doReturn(productAttributes)
            .when(productFieldsIPCClient).getForObject(Mockito.contains("12345678"), Mockito.any(Class.class));
        Mockito
            .doReturn(price)
            .when(priceDAO).getItemById(Mockito.contains("12345678"));

        Mockito
            .doReturn(productAttributes)
            .when(productFieldsIPCClient).getForObject(Mockito.contains("321"), Mockito.any(Class.class));
        Mockito
            .doThrow(new NullPointerException("Data interrupted"))
            .when(priceDAO).getItemById("321");

        Mockito
            .doThrow(new IPCClientException("Service not available", 503))
            .when(productFieldsIPCClient).getForObject(Mockito.contains("432"), Mockito.any(Class.class));
        Mockito
            .doReturn(price)
            .when(priceDAO).getItemById("432");

        Mockito
            .doThrow(new IPCClientException("Product not found", 404))
            .when(productFieldsIPCClient).getForObject(Mockito.contains("543"), Mockito.any(Class.class));
        Mockito
            .doReturn(null)
            .when(priceDAO).getItemById("543");

        Mockito
            .doReturn(productAttributes)
            .when(productFieldsIPCClient).getForObject(Mockito.contains("654"), Mockito.any(Class.class));
        Mockito
            .doReturn(price)
            .when(priceDAO).getItemById("654");

        Mockito
            .doThrow(new NullPointerException("Connection interrupted"))
            .when(priceDAO).addItemAtId(Mockito.matches("765"), Mockito.any(Price.class));
    }

}