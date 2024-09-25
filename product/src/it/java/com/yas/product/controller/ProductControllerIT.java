package com.yas.product.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.yas.commonlibrary.AbstractControllerIT;
import com.yas.commonlibrary.IntegrationTestConfiguration;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductRelated;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.service.ProductService;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductVariationPostVm;
import com.yas.product.viewmodel.product.ProductVariationPutVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
import io.restassured.specification.RequestSpecification;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIT extends AbstractControllerIT {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private ProductOptionRepository productOptionRepository;
    @Autowired
    private ProductRelatedRepository productRelatedRepository;

    private Product productOne;
    private Product productTwo;
    private ProductRelated productRelated;
    private Brand brand;
    private Category category;
    private ProductPostVm productPostVm;
    private ProductPutVm productPutVm;

    private static final String PRODUCT_BACKOFFICE_URL = "/v1/backoffice/products";
    private static final String PRODUCT_STOREFRONT_URL = "/v1/storefront/products";

    void initBrandData() {
        brand = new Brand();
        brand.setName("Brand");
        brand.setSlug("brand-slug");
        brandRepository.save(brand);
    }

    void initCategoryData() {
        category = new Category();
        category.setName("Category");
        categoryRepository.save(category);
    }

    void initProductData() {
        initBrandData();
        productOne = Product.builder()
            .name("Product-1")
            .slug("product-1-slug")
            .sku("sku-1")
            .gtin("gtin-1")
            .brand(brand)
            .stockQuantity(1000L)
            .isFeatured(true)
            .isVisibleIndividually(true)
            .isPublished(true)
            .stockTrackingEnabled(true)
            .parent(productTwo)
            .build();
        productTwo = Product.builder()
            .name("Product-2")
            .slug("product-2-slug")
            .brand(brand)
            .isFeatured(true)
            .isVisibleIndividually(true)
            .isPublished(true)
            .build();

        productRepository.saveAll(List.of(productOne, productTwo));
    }

    void initProductRelativeData() {
        productRelatedRepository.saveAll(List.of(new ProductRelated(3L, productOne, productTwo)));
    }

    void initProductPostVm() {
        initBrandData();
        initCategoryData();

        ProductVariationPostVm productVariationPostVm = new ProductVariationPostVm(
            "Sample Product", "sample-product", "SKU12345", "GTIN1234567890",
            29.99, 101L, new ArrayList<>(), new HashMap<>());

        ProductOption productOption = new ProductOption();
        productOption.setName("Product-option");
        productOptionRepository.save(productOption);

        ProductOptionValuePostVm productOptionValuePostVm = new ProductOptionValuePostVm(
            productOption.getId(),
            "Visible",
            1,
            new ArrayList<>()
        );

        productPostVm = new ProductPostVm(
            "Sample Product Name", "sample-product-slug", brand.getId(),
            List.of(category.getId()), "Short description", "Detailed description",
            "Product specifications", "SKU123456", "GTIN1234567890123", 19.99,
            true, true, false, true, true,
            "Meta Title", "meta", "Meta description ", null,
            List.of(789L, 101L, 112L), new ArrayList<>(List.of(productVariationPostVm)),
            new ArrayList<>(List.of(productOptionValuePostVm)), List.of(234L, 567L), 89L);
    }

    void initProductPutVm() {
        ProductVariationPutVm productVariationPutVm = new ProductVariationPutVm(
            1L, "Sample Product", "sample-product", "SKU12345",
            "GTIN1234", 29.99, 101L, new ArrayList<>(), new HashMap<>());

        productPutVm = new ProductPutVm(
            "Sample Update", "sample-update", 29.99, true, true,
            false, true, true, brand.getId(), new ArrayList<>(),
            "Short description", "Detailed description", "Specifications",
            "SKU123456", "GTIN123456", "Meta Title", "Meta", "Meta",
            10L, new ArrayList<>(), new ArrayList<>(List.of(productVariationPutVm)),
            new ArrayList<>(), new ArrayList<>(), 2L);
    }

    @AfterEach
    public void clearTestData() {
        productRelatedRepository.deleteAll();
        productOptionRepository.deleteAll();
        productImageRepository.deleteAll();
        productCategoryRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();
        brandRepository.deleteAll();
    }

    @Test
    void test_getProductListBackoffice_shouldReturnProductList() {
        initProductData();
        getGivenSpecificationWithAdmin()
            .when()
            .get(PRODUCT_BACKOFFICE_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("productContent", hasSize(2))
            .log().ifValidationFails();
    }

    @Test
    void test_getProductFeature_shouldReturnProductList() {
        initProductData();
        getGivenSpecificationWithAdmin()
            .when()
            .get(PRODUCT_STOREFRONT_URL + "/featured")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("productList", hasSize(2))
            .log().ifValidationFails();
    }

    @Test
    void test_getProductFeatureById_shouldReturnProductList() {
        initProductData();
        Long productOneId = productOne.getId();
        Long productTwoId = productTwo.getId();

        getGivenSpecificationWithAdmin()
            .param("productId", List.of(productOneId, productTwoId))
            .when()
            .get(PRODUCT_STOREFRONT_URL + "/list-featured")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(2))
            .log().ifValidationFails();
    }

    @Test
    void test_getProductByBrand_shouldReturnProducts() {
        initProductData();
        String brandSlug = brand.getSlug();

        getGivenSpecificationWithAdmin()
            .pathParam("brandSlug", brandSlug)
            .when()
            .get("/v1/storefront/brand/{brandSlug}/products")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(2))
            .log().ifValidationFails();
    }

    @Test
    void test_getProductById_shouldReturnProduct() {
        initProductData();
        Long productId = productOne.getId();
        String name = productOne.getName();

        getGivenSpecificationWithAdmin()
            .pathParam("productId", productId)
            .when()
            .get(PRODUCT_BACKOFFICE_URL + "/{productId}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(name))
            .log().ifValidationFails();
    }

    @Test
    void test_getProductSlugStorefrontById_shouldReturnProduct() {
        initProductData();
        Long productId = productOne.getId();
        String slug = productOne.getSlug();

        getGivenSpecificationWithAdmin()
            .pathParam("id", productId)
            .when()
            .get("/v1/storefront/productions/{id}/slug")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("slug", equalTo(slug))
            .log().ifValidationFails();
    }

    @Test
    void test_getProductByIds_shouldReturnProduct() {
        initProductData();
        Long productId = productOne.getId();

        getGivenSpecificationWithAdmin()
            .param("ids", List.of(productId))
            .when()
            .get(PRODUCT_BACKOFFICE_URL + "/by-ids")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_getProductSlugById_shouldReturnProduct() {
        initProductData();
        Long productId = productOne.getId();
        String slug = productOne.getSlug();

        getGivenSpecificationWithAdmin()
            .pathParam("productId", productId)
            .when()
            .get(PRODUCT_BACKOFFICE_URL + "/{productId}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("slug", equalTo(slug))
            .log().ifValidationFails();
    }

    @Test
    void test_getProductDetail_shouldReturnProduct() {
        initProductData();
        Long productId = productOne.getId();
        String name = productOne.getName();

        getGivenSpecificationWithAdmin()
            .pathParam("productId", productId)
            .when()
            .get("/v1/storefront/products-es/{productId}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(name))
            .log().ifValidationFails();
    }

    @Test
    void test_getProductDetailWithSlug_shouldReturnProduct() {
        initProductData();
        String slug = productOne.getSlug();
        String name = productOne.getName();

        getGivenSpecificationWithAdmin()
            .pathParam("slug", slug)
            .when()
            .get("/v1/storefront/product/{slug}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(name))
            .log().ifValidationFails();
    }

    @Test
    void test_createProduct_shouldSuccess() {
        initProductPostVm();
        String name = productPostVm.name();

        getGivenSpecificationWithAdmin()
            .body(productPostVm)
            .when()
            .post(PRODUCT_BACKOFFICE_URL)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("name", equalTo(name))
            .log().ifValidationFails();
    }

    @Test
    void test_updateProduct_shouldSuccess() {
        initProductData();
        initProductPutVm();
        Long productId = productOne.getId();

        getGivenSpecificationWithAdmin()
            .pathParam("id", productId)
            .body(productPutVm)
            .when()
            .put(PRODUCT_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();
    }

    @Test
    void test_exportProduct_shouldSuccess() {
        initProductData();
        String productName = productOne.getName();
        String brandName = brand.getName();

        getGivenSpecificationWithAdmin()
            .param("product-name", productName)
            .param("brand-name", brandName)
            .when()
            .get("/v1/backoffice/export/products")
            .then()
            .statusCode(HttpStatus.OK.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getRelatedProductBackoffice_shouldReturnProducts() {
        initProductData();
        initProductRelativeData();
        Long productId = productOne.getId();

        getGivenSpecificationWithAdmin()
            .pathParam("id", productId)
            .when()
            .get(PRODUCT_BACKOFFICE_URL + "/related-products/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_getRelatedProductStorefront_shouldReturnProducts() {
        initProductData();
        initProductRelativeData();
        Long productId = productOne.getId();

        getGivenSpecificationWithAdmin()
            .pathParam("id", productId)
            .when()
            .get(PRODUCT_STOREFRONT_URL + "/related-products/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("productContent", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_getProductForWarehouse_shouldReturnProduct() {
        initProductData();
        String name = productOne.getName();
        String sku = productOne.getSku();

        getGivenSpecificationWithAdmin()
            .param("name", name)
            .param("sku", sku)
            .param("selection", "ALL")
            .when()
            .get(PRODUCT_BACKOFFICE_URL + "/for-warehouse")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("productContent", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_updateProductQuantity_shouldSuccess() {
        initProductData();
        ProductQuantityPostVm productQuantityPostVm = new ProductQuantityPostVm(productOne.getId(), 1000L);

        getGivenSpecificationWithAdmin()
            .body(List.of(productQuantityPostVm))
            .when()
            .put(PRODUCT_BACKOFFICE_URL + "/update-quantity")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();
    }

    @Test
    void test_subtractProductQuantity_shouldSuccess() {
        initProductData();
        ProductQuantityPutVm productQuantityPutVm = new ProductQuantityPutVm(productOne.getId(), 500L);

        getGivenSpecificationWithAdmin()
            .body(List.of(productQuantityPutVm))
            .when()
            .put(PRODUCT_BACKOFFICE_URL + "/subtract-quantity")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();

    }

    private RequestSpecification getGivenSpecificationWithAdmin() {
        return given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"));
    }
}
