package com.myretail.api;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static com.jayway.restassured.module.mockmvc.config.AsyncConfig.withTimeout;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import com.myretail.api.model.Price;
import com.myretail.api.model.ProductAttributes;
import com.myretail.api.service.ProductService;
import com.myretail.core.app.AppSpringConfiguration;
import com.myretail.core.datastore.DataAccessObject;
import com.myretail.core.ipc.IPCClient;

//@ActiveProfiles("unit-test")
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
    public void testHappyPath() {

        given().accept(ContentType.JSON).when().async().timeout(5000).get("/product/13860428").then().apply(print())
                .contentType(ContentType.JSON).statusCode(200).body("id", equalTo("13860428"));

    }

    @Test
    public void testBadRequest() {

        given().accept(ContentType.JSON).when().get("/product/1/1").then().apply(print()).statusCode(404);

    }

    public void setupData() {

        ProductAttributes productAttributes = new ProductAttributes();
        productAttributes.setId("12345678");
        productAttributes.setAttributes(Collections.unmodifiableMap(new HashMap<String, String>() {{
            put("description", "Twin Peaks CD");
        }}));

        Mockito
            .doReturn(productAttributes)
            .when(productFieldsIPCClient).getForObject(Mockito.anyString(), Mockito.any(Class.class));

        Price price = new Price(new BigDecimal(49.01), "USD");
        
        Mockito
            .doReturn(price)
            .when(priceDAO).getItemById(Mockito.anyString());

    }

}