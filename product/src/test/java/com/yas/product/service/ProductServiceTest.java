package com.yas.product.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.ProductApplication;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ProductApplication.class)
class ProductServiceTest {
    private final ZonedDateTime CREATED_ON = ZonedDateTime.now();
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @MockBean
    private MediaService mediaService;
    @Autowired
    private ProductService productService;
    private List<Product> products;
    private List<Category> categoryList;
    private List<ProductCategory> productCategoryList;
    private Category category1;
    private Category category2;
    private Brand brand1;
    private Brand brand2;
    private int pageNo = 0;
    private int pageSize = 5;
    private int totalPage = 2;
    private NoFileMediaVm noFileMediaVm;

    @BeforeEach
    void setUp() {
        noFileMediaVm = new NoFileMediaVm(1L, "caption", "fileName", "mediaType", "url");
        when(mediaService.getMedia(1L)).thenReturn(noFileMediaVm);
    }

    private void generateTestData() {
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
    void tearDown() {
        productCategoryRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();
        brandRepository.deleteAll();
    }

    @DisplayName("Get product feature success then return list ProductThumbnailVm")
    @Test
    void getFeaturedProducts_WhenEverythingIsOkay_Success() {
        generateTestData();
        ProductFeatureGetVm actualResponse = productService.getListFeaturedProducts(pageNo, pageSize);
        assertThat(actualResponse.totalPage()).isEqualTo(totalPage);
        assertThat(actualResponse.productList().size()).isEqualTo(5);
        Map<String, Product> productMap = products.stream().collect(Collectors.toMap(Product::getSlug, product -> product));
        for (int i = 0; i < actualResponse.productList().size(); i++) {
            ProductThumbnailGetVm productThumbnailGetVm = actualResponse.productList().get(i);
            Product product = productMap.get(productThumbnailGetVm.slug());
            assertEquals(product.getName(), actualResponse.productList().get(i).name());
        }
    }


    @DisplayName("Get products by brand when brand is available with slug then success")
    @Test
    void getProductsByBrand_BrandSlugIsValid_Success() {
        generateTestData();
        List<ProductThumbnailVm> actualResponse = productService.getProductsByBrand(brand1.getSlug());
        assertEquals(5, actualResponse.size());
    }

    @DisplayName("Get products by brand when brand is non exist then throws exception")
    @Test
    void getProductsByBrand_BrandIsNonExist_ThrowsNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.getProductsByBrand("brandSlug1"));
        assertEquals(String.format("Brand %s is not found", "brandSlug1"), exception.getMessage());
    }

    @Test
    void getProduct_whenProductIdInvalid_shouldThrowException() {
        Long id = 9999L;
        Exception notFoundException = assertThrows(NotFoundException.class, () -> productService.getProductById(id));
        assertEquals(String.format("Product %s is not found", id), notFoundException.getMessage());
    }

    @Test
    void getProduct_whenProductIdValid_shouldSuccess() {
        generateTestData();
        List<Product> productDbList = productRepository.findAll();
        assertNotNull(productService.getProductById(productDbList.getFirst().getId()));
    }

    @Test
    void getListFeaturedProductsByListProductIds_whenAllProductIdsValid_shouldSuccess() {
        generateTestData();
        List<Product> productDbList = productRepository.findAll();
        List<Long> ids = productDbList.stream().map(Product::getId).collect(Collectors.toList());
        assertEquals(10, productService.getFeaturedProductsById(ids).size());
    }

    @Test
    void getProductsWithFilter_WhenFilterByBrandNameAndProductName_ThenSuccess() {
        generateTestData();
        ProductListGetVm actualResponse = productService.getProductsWithFilter(pageNo, pageSize, "product1", brand1.getName());
        assertEquals(1, actualResponse.productContent().size());
    }

    @Test
    void getProductsWithFilter_WhenFilterByBrandName_ThenSuccess() {
        generateTestData();
        ProductListGetVm actualResponse = productService.getProductsWithFilter(pageNo, pageSize, "", brand1.getName());
        assertEquals(5, actualResponse.productContent().size());
    }

    @Test
    void getProductsWithFilter_WhenFilterByProductName_ThenSuccess() {
        generateTestData();
        ProductListGetVm actualResponse = productService.getProductsWithFilter(pageNo, pageSize, "product9", "");
        assertEquals(1, actualResponse.productContent().size());
    }

    @Test
    void getProductsWithFilter_whenFindAll_thenSuccess() {
        generateTestData();
        ProductListGetVm actualResponse = productService.getProductsWithFilter(pageNo, pageSize, "product", brand2.getName());
        assertEquals(5, actualResponse.productContent().size());

    }

    @Test
    void getProductsFromCategory_WhenFindAllByCategory_ThenSuccess() {
        generateTestData();
        ProductListGetFromCategoryVm actualResponse = productService.getProductsFromCategory(pageNo, pageSize, "categorySlug1");
        assertEquals(5, actualResponse.productContent().size());
    }

    @Test
    void getProductsFromCategory_CategoryIsNonExist_ThrowsNotFoundException() {
        generateTestData();
        String categorySlug = "laptop-macbook";
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.getProductsFromCategory(pageNo, pageSize, categorySlug));
        assertThat(exception.getMessage()).isEqualTo(String.format("Category %s is not found", categorySlug));
    }

    @Test
    void deleteProduct_givenProductIdValid_thenSuccess() {
        generateTestData();
        Long id = productRepository.findAll().getFirst().getId();
        productService.deleteProduct(id);
        Optional<Product> result = productRepository.findById(id);
        // Soft delete, set published to false
        assertTrue(result.isPresent());
        assertFalse(result.get().isPublished());

    }

    @Test
    void deleteProduct_givenProductIdInvalid_thenThrowNotFoundException() {
        Long productId = 99999L;
        assertThrows(NotFoundException.class, () -> productService.deleteProduct(productId));
    }

    @Test
    void getProductsByMultiQuery_WhenFilterByBrandNameAndProductName_ThenSuccess() {
        generateTestData();
        Double startPrice = 1.0;
        Double endPrice = 10.0;
        String productName = "product2";
        ProductsGetVm result = productService.getProductsByMultiQuery(pageNo, pageSize, productName, category2.getSlug(), startPrice, endPrice);

        // Assert result
        assertEquals(1, result.productContent().size());
        ProductThumbnailGetVm thumbnailGetVm = result.productContent().getFirst();
        assertEquals("product2", thumbnailGetVm.name());
        assertEquals("slug2", thumbnailGetVm.slug());
        assertEquals(10.0, thumbnailGetVm.price());
        assertEquals(pageNo, result.pageNo());
        assertEquals(pageSize, result.pageSize());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        assertTrue(result.isLast());
    }
}