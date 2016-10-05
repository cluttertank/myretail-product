# myretail-product
It's a RESTful service that retrieves product name and current price by ID.
Request: GET /product/{id} (where {id} is a number)
Example response:
{
  "id": 12345678,
  "name": "Z-Boy 2000",
  "current_price": {
    "value": 99.01,
    "currency_code": "USD"
  }
}

It reads the product name from and external service: https://github.com/cluttertank/myretail-product-attributes
And reads the pricing information from an in-memory map based data store and combines it all together in a single response.

It also hosts another service which accepts the price info and stores it in the data store
Request: PUT /product/{id} (with body structure same as the GET response)

# How To
Requirements:
* Java 8
* Tomcat 8 (or 9)

To Run:
* set CATALINA_HOME as your tomcat home directory
* set RUNTIME_HOME as your current directory
* unizip how-to/myretail-product-attributes.zip and how-to/myretail-product in RUNTIME_HOME
* copy how-to/startup.bat to RUNTIME_HOME
* run startup.bat myretail-product-attributes 8081
* run startup.bat myretail-product 8080

# Test Reports
* test-reports.zip
    - Contains and jacoco code-coverage report and unit test reports
* test-results.zip
    - Unit test results

# Features
* Resilience
    - features use of Hystrix command to ensure resilience
* asynch processing
    - Use of defferred result helps freeing up of the io threads
* RxJava
    - Parallel processing
    - Allows a more functional way of programming
    - Supported by hystrix command inherently
* unit-testing
    - Over 90% of code coverage
    - Employs RestAssured to test rest interface by mocking spring mvc
* monitoring
    - includes a healthcheck url
* java 8
    - Use of lambdas and streams

# Areas of improvement
* Security
    - request authorization / authentication - LDAP, etc
    - SSL
* Code-quality
    - sonar
* Metrics reporting Aspect
    - jvm
    - custom
    - hystrix
* Logging
    - logging aspect
    - request-id
    - MDC
    - request/response
* configuration
    - properties encryption
    - runtime dynamic loading - zookeeper
    - internationalization of messages
* Service registry
* Service discovery
* Persisten noSQL DB