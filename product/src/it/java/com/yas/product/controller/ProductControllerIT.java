package com.yas.product.controller;

import static org.instancio.Select.field;
import static org.hamcrest.Matchers.hasSize;

import com.yas.product.config.IntegrationTestConfiguration;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import io.restassured.RestAssured;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@Import(IntegrationTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIT extends AbstractControllerIT{
    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    BrandRepository brandRepository;

    private List<Product> products;
    private List<Category> categoryList;
    private List<ProductCategory> productCategoryList;
    private Category category1;
    private Category category2;
    private Brand brand1;
    private Brand brand2;
    private NoFileMediaVm noFileMediaVm;

    final String PRODUCT_BACKOFFICE_BASE_URL = "/v1/backoffice/products";
    final ZonedDateTime CREATED_ON = ZonedDateTime.now();

    @BeforeEach
    void setUp(){
        brand1 = new Brand();
        brand1.setName("phone1");
        brand1.setSlug("brandSlug1");
        brand1.setPublished(true);
        brand2 = new Brand();
        brand2.setName("phone2");
        brand2.setSlug("brandSlug2");
        brand2.setPublished(true);
        brandRepository.saveAll(List.of(brand1, brand2));

        category1 = new Category();
        category1.setName("category1");
        category1.setSlug("categorySlug1");
        category2 = new Category();
        category2.setName("category2");
        category2.setSlug("categorySlug2");

        categoryList = List.of(category1, category2);
        categoryRepository.saveAll(categoryList);

        products = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Product product = Product.builder()
                .name(String.format("product%s", i))
                .slug(String.format("slug%s", i))
                .isAllowedToOrder(true)
                .isPublished(true)
                .isFeatured(true)
                .isVisibleIndividually(true)
                .stockTrackingEnabled(true)
                .thumbnailMediaId(1L)
                .taxClassId(1L)
                .build();
            if (i % 2 == 0) {
                product.setBrand(brand2);
                product.setPrice(10.0);
            } else {
                product.setBrand(brand1);
                product.setPrice(5.0);
            }
            product.setCreatedOn(CREATED_ON);
            products.add(product);
        }
        List<Product> productsDB = productRepository.saveAll(products);

        productCategoryList = new ArrayList<>();
        for (int i = 1; i <= productsDB.size(); i++) {
            Product productDB = productsDB.get(i - 1);
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProduct(productDB);
            if (i % 2 == 0) {
                productCategory.setCategory(category2);
            } else {
                productCategory.setCategory(category1);
            }
            productCategoryList.add(productCategory);
        }
        productCategoryRepository.saveAll(productCategoryList);
    }

    @AfterEach
    void tearDown(){
        productCategoryRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        brandRepository.deleteAll();
    }

    @Test
    void testGetProductForOrder_shouldReturn401_whenNotGivenToken(){
        RestAssured.given(getRequestSpecification())
            .queryParam("productIds", products.get(0).getId(), products.get(1).getId())
            .when()
            .get(PRODUCT_BACKOFFICE_BASE_URL+"/for-order")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .log().ifValidationFails();
    }

    @Test
    void testGetProductForOrder_shouldReturnData_whenGivenToken(){
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .queryParam("productIds", products.get(0).getId(), products.get(1).getId())
            .when()
            .get(PRODUCT_BACKOFFICE_BASE_URL+"/for-order")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(2))
            .log().ifValidationFails();
    }

    @Test
    void testGetProductForOrder_shouldReturnData_whenGivenTokenAndOneEntryDoesNotExist(){
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .queryParam("productIds", products.get(0).getId(), products.get(1).getId(), 0)
            .when()
            .get(PRODUCT_BACKOFFICE_BASE_URL+"/for-order")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(2))
            .log().ifValidationFails();
    }

    @Test
    void testGetProductForOrder_shouldReturnEmpty_whenGivenTokenAndEntriesDoesNotExist(){
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .queryParam("productIds",  0,1000)
            .when()
            .get(PRODUCT_BACKOFFICE_BASE_URL+"/for-order")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(0))
            .log().ifValidationFails();
    }
}
