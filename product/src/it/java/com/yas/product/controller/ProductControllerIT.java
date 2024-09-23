package com.yas.product.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.yas.product.config.AbstractControllerIT;
import com.yas.product.config.IntegrationTestConfiguration;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@Slf4j
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {"com.yas.commonlibrary"})
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
    private Product product;
    private ProductRelated productRelated;
    private ProductOption productOption;
    private ProductQuantityPostVm productQuantityPostVm;
    private ProductQuantityPutVm productQuantityPutVm;
    private Brand brand;
    private Category category;
    private ProductCategory productCategory;
    private ProductVariationPostVm productVariationPostVm;
    private ProductVariationPutVm productVariationPutVm;
    private ProductOptionValuePostVm productOptionValuePostVm;
    private ProductPostVm productPostVm;
    private ProductPutVm productPutVm;

    private static final String PRODUCT_BACKOFFICE_URL = "/v1/backoffice/products";
    private static final String PRODUCT_STOREFRONT_URL = "/v1/storefront/products";

    @BeforeEach
    public void insertTestData() {
        brand = new Brand();
        brand.setName("Brand");
        brand.setSlug("brand-slug");
        brandRepository.save(brand);

        category = new Category();
        category.setName("Category");
        categoryRepository.save(category);

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
            .isFeatured(false)
            .isVisibleIndividually(true)
            .isPublished(true)
            .build();

        product =  Product.builder()
            .name("Example Product")
            .shortDescription("A brief description of the product.")
            .description("A detailed description of the example product, including features and benefits.")
            .specification("Specifications: Size - Medium, Color - Red, Material - Plastic.")
            .sku("EX1234")
            .gtin("0123456789123")
            .slug("example-product")
            .price(99.99)
            .hasOptions(true)
            .isAllowedToOrder(true)
            .isPublished(true)
            .isFeatured(false)
            .isVisibleIndividually(true)
            .stockTrackingEnabled(true)
            .stockQuantity(50L)
            .taxClassId(1L)
            .metaTitle("Example Product - Best in Class")
            .metaKeyword("example, product, best")
            .metaDescription("Purchase the Example Product, featuring top quality and reliability.")
            .thumbnailMediaId(101L)
            .brand(null)
            .productCategories(new ArrayList<>())
            .attributeValues(new ArrayList<>())
            .productImages(new ArrayList<>())
            .parent(null)
            .products(new ArrayList<>(List.of(productOne, productTwo)))
            .taxIncluded(true)
            .build();

        productRepository.saveAll(List.of(product, productOne, productTwo));

        productRelated = new ProductRelated();
        productRelated.setProduct(productOne);
        productRelated.setRelatedProduct(productTwo);

        productRelatedRepository.saveAll(List.of(new ProductRelated(1L, product, productOne),
            new ProductRelated(2L, product, productTwo)));
        productRelatedRepository.save(productRelated);

        productCategory = new ProductCategory();
        productCategory.setCategory(category);
        productCategory.setProduct(product);
        productCategoryRepository.save(productCategory);

        productVariationPostVm = new ProductVariationPostVm(
            "Sample Product",
            "sample-product",
            "SKU12345",
            "GTIN1234567890",
            29.99,
            101L,
            new ArrayList<>(),
            new HashMap<>()
        );

        productOption = new ProductOption();
        productOption.setName("Product-option");
        productOptionRepository.save(productOption);

        productOptionValuePostVm = new ProductOptionValuePostVm(
            productOption.getId(),
            "Visible",
            1,
            new ArrayList<>()
        );

        productPostVm = new ProductPostVm(
            "Sample Product Name",
            "sample-product-slug",
            brand.getId(),
            List.of(category.getId()),
            "Short description of the product.",
            "Detailed description of the product with more information.",
            "Product specifications including dimensions, weight, and materials.",
            "SKU123456",
            "GTIN1234567890123",
            19.99,
            true,
            true,
            false,
            true,
            true,
            "Meta Title for SEO",
            "meta, keywords, for, SEO",
            "Meta description of the product for search engines.",
            null,
            List.of(789L, 101L, 112L),
            new ArrayList<>(List.of(productVariationPostVm)),
            new ArrayList<>(List.of(productOptionValuePostVm)),
            List.of(234L, 567L),
            89L
        );

        productVariationPutVm = new ProductVariationPutVm(
            1L,
            "Sample Product",
            "sample-product",
            "SKU12345",
            "GTIN1234567890",
            29.99,
            101L,
            new ArrayList<>(),
            new HashMap<>()
        );

        productPutVm = new ProductPutVm(
            "Sample Product Update",
            "sample-product-update",
            29.99,
            true,
            true,
            false,
            true,
            true,
            brand.getId(),
            new ArrayList<>(),
            "Short description of the product.",
            "Detailed description of the product with features.",
            "Specifications of the product.",
            "SKU123456",
            "GTIN1234567890123",
            "Meta Title for SEO",
            "Meta Keywords, Sample, Product",
            "Meta Description of the product for search engines.",
            10L,
            new ArrayList<>(),
            new ArrayList<>(List.of(productVariationPutVm)),
            new ArrayList<>(),
            new ArrayList<>(),
            2L
        );

        productQuantityPostVm = new ProductQuantityPostVm(productOne.getId(), 1000L);
        productQuantityPutVm = new ProductQuantityPutVm(productOne.getId(), 500L);
    }

    @AfterEach
    public void clearTestData() {
        productRelatedRepository.deleteAll();
        productOptionRepository.deleteAll();
        productImageRepository.deleteAll();
        productOptionRepository.deleteAll();
        productCategoryRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();
        brandRepository.deleteAll();
    }

    @Test
    void test_getProductListBackoffice_shouldReturnProductList() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .when()
            .get(PRODUCT_BACKOFFICE_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("productContent", hasSize(2))
            .log().ifValidationFails();
    }

    @Test
    void test_getProductFeature_shouldReturnProductList() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .when()
            .get(PRODUCT_STOREFRONT_URL + "/featured")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("productList", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_getProductFeatureById_shouldReturnProductList() {
        Long productOneId = productOne.getId();
        Long productTwoId = productTwo.getId();

        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
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
        String brandSlug = brand.getSlug();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
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
        Long productId = productOne.getId();
        String name = productOne.getName();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
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
        Long productId = productOne.getId();
        String slug = productOne.getSlug();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
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
        Long productId = productOne.getId();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
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
        Long productId = productOne.getId();
        String slug = productOne.getSlug();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
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
        Long productId = productOne.getId();
        String name = productOne.getName();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
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
        String slug = productOne.getSlug();
        String name = productOne.getName();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
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
        String name = productPostVm.name();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
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
        Long productId = productOne.getId();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
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
        String productName = product.getName();
        String brandName = brand.getName();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .param("product-name", productName)
            .param("brand-name", brandName)
            .body(productPutVm)
            .when()
            .get("/v1/backoffice/export/products")
            .then()
            .statusCode(HttpStatus.OK.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getRelatedProductBackoffice_shouldReturnProducts() {
        Long productId = productOne.getId();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .pathParam("id", productId)
            .body(productPutVm)
            .when()
            .get(PRODUCT_BACKOFFICE_URL + "/related-products/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_getRelatedProductStorefront_shouldReturnProducts() {
        Long productId = productOne.getId();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .pathParam("id", productId)
            .body(productPutVm)
            .when()
            .get(PRODUCT_STOREFRONT_URL + "/related-products/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("productContent", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_getProductForWarehouse_shouldReturnProduct() {
        String name = productOne.getName();
        String sku = productOne.getSku();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .param("name", name)
            .param("sku", sku)
            .param("selection", "ALL")
            .body(productPutVm)
            .when()
            .get(PRODUCT_BACKOFFICE_URL + "/for-warehouse")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("productContent", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_updateProductQuantity_shouldSuccess() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(List.of(productQuantityPostVm))
            .when()
            .put(PRODUCT_BACKOFFICE_URL + "/update-quantity")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();
    }

    @Test
    void test_subtractProductQuantity_shouldSuccess() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(List.of(productQuantityPutVm))
            .when()
            .put(PRODUCT_BACKOFFICE_URL + "/subtract-quantity")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();

    }
}
