package com.yas.search.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.yas.search.config.IntegrationTestConfiguration;
import com.yas.search.model.Product;
import com.yas.search.repository.ProductRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.http.HttpStatus;

@Import(IntegrationTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PropertySource("classpath:application.properties")
public class ProductControllerIT extends AbstractControllerIT {

  @Autowired
  ProductRepository productRepository;

  @BeforeEach
  public void init() {

    Product product = new Product();
    product.setId(1L);
    product.setName("Macbook");

    productRepository.save(product, RefreshPolicy.IMMEDIATE);
  }

  @AfterEach
  public void destroy() {
    productRepository.deleteAll();
  }

  @Test
  public void test_findProductAdvance_shouldReturnSuccessfully() {
    given(getRequestSpecification())
        .auth().oauth2(getAccessToken("admin", "admin"))
        .contentType(ContentType.JSON)
        .queryParam("keyword", "Macbook")
        .queryParam("page", 0)
        .queryParam("size", 12)
        .get("/search/storefront/catalog-search")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("pageNo", equalTo(0))
        .body("pageSize", equalTo(12))
        .body("totalElements", equalTo(1))
        .log()
        .ifValidationFails();
  }

  @Test
  public void test_productSearchAutoComplete_shouldReturnSuccessfully() {
    given(getRequestSpecification())
        .auth().oauth2(getAccessToken("admin", "admin"))
        .contentType(ContentType.JSON)
        .queryParam("keyword", "Macbook")
        .get("/search/storefront/search_suggest")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("productNames", hasSize(1))
        .log()
        .ifValidationFails();
  }
}
