package com.yas.product.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.yas.product.config.AbstractControllerIT;
import com.yas.product.config.IntegrationTestConfiguration;
import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.service.BrandService;
import com.yas.product.viewmodel.brand.BrandPostVm;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BrandControllerIT extends AbstractControllerIT {

    @Autowired
    private BrandService brandService;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductRepository productRepository;

    private Brand brandOne;
    private Brand brandTwo;
    private Product product;

    private static final String BRAND_BACKOFFICE_URL = "/v1/backoffice/brands";
    private static final String BRAND_STOREFRONT_URL = "/v1/storefront/brands";

    @BeforeEach
    public void insertTestData() {
        product = Product.builder()
            .name("Product")
            .build();

        brandOne = new Brand();
        brandOne.setName("Brand-One");
        brandOne.setSlug("brand-1-slug");

        brandTwo = new Brand();
        brandTwo.setName("Brand-Two");
        brandTwo.setSlug("brand-2-slug");

        brandOne = brandRepository.save(brandOne);

        brandTwo.setProducts(new ArrayList<>(List.of(product)));

        brandTwo = brandRepository.save(brandTwo);
        product = productRepository.save(product);
    }

    @AfterEach
    public void clearTestData() {
        brandRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void test_createBrand_thenSuccess_whenProvideValidData() {
        BrandPostVm brandPostVm = new BrandPostVm("Brand-One1", "brand-one1", true);
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .body(brandPostVm)
            .when()
            .post(BRAND_BACKOFFICE_URL)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("name", equalTo(brandPostVm.name()))
            .log().ifValidationFails();
    }

    @Test
    void test_createBrand_thenReturn400_whenProvideExistedName() {
        BrandPostVm brandPostVm = new BrandPostVm("Brand-One", "brand-one", true);
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .body(brandPostVm)
            .when()
            .post(BRAND_BACKOFFICE_URL)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .log().ifValidationFails();
    }

    @Test
    void test_updateBrand_thenSuccess_whenProvideValidData() {
        BrandPostVm brandPostVm = new BrandPostVm("Brand-One1", "brand-one1", true);
        Long brandId = brandOne.getId();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", brandId)
            .body(brandPostVm)
            .when()
            .put(BRAND_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();
    }

    @Test
    void test_updateBrand_thenReturn404_whenBrandNotFound() {
        BrandPostVm brandPost = new BrandPostVm("Brand-One1", "brand-one1", true);
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", 1000L)
            .body(brandPost)
            .when()
            .put(BRAND_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getBrand_thenReturn403_whenProvideInvalidAccessToken() {
        given(getRequestSpecification())
            .when()
            .get(BRAND_BACKOFFICE_URL)
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getListBrandsBackoffice_shouldReturnListBrands() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .param("brandName", "a")
            .when()
            .get(BRAND_BACKOFFICE_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(2))
            .log().ifValidationFails();
    }

    @Test
    void test_getListBrandsStorefront_shouldReturnListBrands() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .param("brandName", "a")
            .when()
            .get(BRAND_STOREFRONT_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(2))
            .log().ifValidationFails();
    }

    @Test
    void test_getPageableBrandBackoffice_shouldReturnBrands() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .param("pageNo", 0)
            .param("pageSize", 2)
            .when()
            .get(BRAND_BACKOFFICE_URL + "/paging")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("brandContent", hasSize(2))
            .log().ifValidationFails();
    }

    @Test
    void test_getPageableBrandStorefront_shouldReturnBrands() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .param("pageNo", 0)
            .param("pageSize", 2)
            .when()
            .get(BRAND_STOREFRONT_URL + "/paging")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("brandContent", hasSize(2))
            .log().ifValidationFails();
    }

    @Test
    void test_getBrandById_shouldReturnBrand_whenProvideValidId() {
        Long brandId = brandOne.getId();
        String name = brandOne.getName();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", brandId)
            .when()
            .get(BRAND_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(name))
            .log().ifValidationFails();
    }

    @Test
    void test_getBrandById_shouldReturn404_whenNotFound() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", 10000L)
            .when()
            .get(BRAND_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteBrand_shouldDeleteSuccess_whenProvideValidId() {
        Long brandId = brandOne.getId();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", brandId)
            .when()
            .delete(BRAND_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteBrand_shouldReturn404_whenNotFound() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", 10000L)
            .when()
            .delete(BRAND_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getListBrandsByIds_shouldReturnListBrands() {
        Long brandOneId = brandOne.getId();
        Long brandTwoId = brandTwo.getId();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .param("ids", List.of(brandOneId, brandTwoId))
            .when()
            .get(BRAND_BACKOFFICE_URL+ "/by-ids")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(2))
            .log().ifValidationFails();
    }
}
