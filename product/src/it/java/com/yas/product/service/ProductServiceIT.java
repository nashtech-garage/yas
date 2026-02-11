package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.IntegrationTestConfiguration;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.model.AbstractAuditEntity;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductThumbnailGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(IntegrationTestConfiguration.class)
class ProductServiceIT {
    private final ZonedDateTime CREATED_ON = ZonedDateTime.now();
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private ProductOptionCombinationRepository productOptionCombinationRepository;
    @Autowired
    private ProductOptionValueRepository productOptionValueRepository;
    @Autowired
    private ProductOptionRepository productOptionRepository;
    @MockitoBean
    private MediaService mediaService;
    @Autowired
    private ProductService productService;
    private List<Product> products;
    private List<Category> categoryList;
    private List<ProductCategory> productCategoryList;
    private List<ProductOption> productOptions;
    private Category category1;
    private Category category2;
    private Brand brand1;
    private Brand brand2;
    private final int pageNo = 0;
    private final int pageSize = 5;
    private final int totalPage = 2;
    private NoFileMediaVm noFileMediaVm;

    @BeforeEach
    void setUp() {
        noFileMediaVm = new NoFileMediaVm(1L, "caption", "fileName", "mediaType", "url");
        when(mediaService.getMedia(1L)).thenReturn(noFileMediaVm);
        generateTestData();
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
            product.setCreatedOn(CREATED_ON.minusDays(i));
            products.add(product);
        }
        productRepository.saveAll(products);

        productCategoryList = new ArrayList<>();
        for (int i = 1; i <= products.size(); i++) {
            Product product = products.get(i - 1);
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProduct(product);
            if (i % 2 == 0) {
                productCategory.setCategory(category2);
            } else {
                productCategory.setCategory(category1);
            }
            productCategoryList.add(productCategory);
        }
        productCategoryRepository.saveAll(productCategoryList);

        initProductOptions();
    }

    private void initProductOptions() {
        ProductOption productOption1 = new ProductOption();
        productOption1.setName("productOption1");
        ProductOption productOption2 = new ProductOption();
        productOption2.setName("productOption2");
        ProductOption productOption3 = new ProductOption();
        productOption3.setName("productOption3");
        productOptions = List.of(productOption1, productOption2, productOption3);
        productOptionRepository.saveAll(productOptions);
    }

    private Product getVariant(Product parent) {
        return Product.builder()
                .name("product variant name")
                .slug("product variant slug")
                .isAllowedToOrder(true)
                .isPublished(true)
                .isFeatured(true)
                .isVisibleIndividually(true)
                .stockTrackingEnabled(true)
                .thumbnailMediaId(1L)
                .taxClassId(1L)
                .parent(parent)
                .build();
    }

    @AfterEach
    void tearDown() {
        productOptionCombinationRepository.deleteAll();
        productOptionValueRepository.deleteAll();
        productOptionRepository.deleteAll();
        productCategoryRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();
        brandRepository.deleteAll();
    }

    @DisplayName("Get product feature success then return list ProductThumbnailVm")
    @Test
    void getFeaturedProducts_WhenEverythingIsOkay_Success() {
        ProductFeatureGetVm actualResponse = productService.getListFeaturedProducts(pageNo, pageSize);
        assertThat(actualResponse.totalPage()).isEqualTo(totalPage);
        assertThat(actualResponse.productList()).hasSize(5);
        Map<String, Product> productMap
                = products.stream().collect(Collectors.toMap(Product::getSlug, product -> product));
        for (int i = 0; i < actualResponse.productList().size(); i++) {
            ProductThumbnailGetVm productThumbnailGetVm = actualResponse.productList().get(i);
            Product product = productMap.get(productThumbnailGetVm.slug());
            assertEquals(product.getName(), actualResponse.productList().get(i).name());
        }
    }

    @DisplayName("Get products by brand when brand is available with slug then success")
    @Test
    void getProductsByBrand_BrandSlugIsValid_Success() {
        List<ProductThumbnailVm> actualResponse = productService.getProductsByBrand(brand1.getSlug());
        assertEquals(5, actualResponse.size());
    }

    @DisplayName("Get products by brand when brand is non exist then throws exception")
    @Test
    void getProductsByBrand_BrandIsNonExist_ThrowsNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> productService.getProductsByBrand("nonExistBrandSlug1"));
        assertEquals(String.format("Brand %s is not found", "nonExistBrandSlug1"), exception.getMessage());
    }

    @Test
    void getProduct_whenProductIdInvalid_shouldThrowException() {
        Long id = 9999L;
        Exception notFoundException = assertThrows(NotFoundException.class, () -> productService.getProductById(id));
        assertEquals(String.format("Product %s is not found", id), notFoundException.getMessage());
    }

    @Test
    void getProduct_whenProductIdValid_shouldSuccess() {
        List<Product> productDbList = productRepository.findAll();
        assertNotNull(productService.getProductById(productDbList.getFirst().getId()));
    }

    @Test
    void getListFeaturedProductsByListProductIds_whenAllProductIdsValid_shouldSuccess() {
        List<Product> productDbList = productRepository.findAll();
        List<Long> ids = productDbList.stream().map(Product::getId).collect(Collectors.toList());
        assertEquals(10, productService.getFeaturedProductsById(ids).size());
    }

    @Test
    void getProductsWithFilter_WhenFilterByBrandNameAndProductName_ThenSuccess() {
        ProductListGetVm actualResponse
                = productService.getProductsWithFilter(pageNo, pageSize, "product1", brand1.getName());
        assertEquals(1, actualResponse.productContent().size());
    }

    @Test
    void getProductsWithFilter_WhenFilterByBrandName_ThenSuccess() {
        ProductListGetVm actualResponse = productService.getProductsWithFilter(pageNo, pageSize, "", brand1.getName());
        assertEquals(5, actualResponse.productContent().size());
    }

    @Test
    void getProductsWithFilter_WhenFilterByProductName_ThenSuccess() {
        ProductListGetVm actualResponse = productService.getProductsWithFilter(pageNo, pageSize, "product9", "");
        assertEquals(1, actualResponse.productContent().size());
    }

    @Test
    void getProductsWithFilter_whenFindAll_thenSuccess() {
        ProductListGetVm actualResponse
                = productService.getProductsWithFilter(pageNo, pageSize, "product", brand2.getName());
        assertEquals(5, actualResponse.productContent().size());
    }

    @Test
    void getProductsFromCategory_WhenFindAllByCategory_ThenSuccess() {
        ProductListGetFromCategoryVm actualResponse
                = productService.getProductsFromCategory(pageNo, pageSize, "categorySlug1");
        assertEquals(5, actualResponse.productContent().size());
    }

    @Test
    void getProductsFromCategory_CategoryIsNonExist_ThrowsNotFoundException() {
        String categorySlug = "laptop-macbook";
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> productService.getProductsFromCategory(pageNo, pageSize, categorySlug));
        assertThat(exception.getMessage()).isEqualTo(String.format("Category %s is not found", categorySlug));
    }

    @Test
    void deleteProduct_givenParentProduct_thenSuccess() {
        Long id = productRepository.findAll().getFirst().getId();
        productService.deleteProduct(id);
        Optional<Product> result = productRepository.findById(id);
        // Soft delete, set published to false
        assertTrue(result.isPresent());
        assertFalse(result.get().isPublished());
    }

    @Test
    void deleteProduct_givenVariant_thenSuccess() {
        String value = "red";
        ProductOption productOption = new ProductOption();
        productOption.setName(value);

        String cpu = "I5";
        ProductOption productOption2 = new ProductOption();
        productOption2.setName(cpu);

        ProductOptionCombination productOptionCombination = new ProductOptionCombination();
        productOptionCombination.setValue(value);
        productOptionCombination.setDisplayOrder(1);

        ProductOptionCombination productOptionCombination2 = new ProductOptionCombination();
        productOptionCombination2.setValue(cpu);
        productOptionCombination2.setDisplayOrder(1);

        Product parent = productRepository.findAll().getFirst();
        Product product = getVariant(parent);
        Product afterProduct = productRepository.save(product);

        productOptionCombination.setProduct(afterProduct);
        ProductOption afterProductOption = productOptionRepository.save(productOption);
        productOptionCombination.setProductOption(afterProductOption);

        productOptionCombination2.setProduct(afterProduct);
        ProductOption afterProductOption2 = productOptionRepository.save(productOption2);
        productOptionCombination2.setProductOption(afterProductOption2);

        ProductOptionCombination productOptionCombinationSaved
                = productOptionCombinationRepository.save(productOptionCombination);
        ProductOptionCombination productOptionCombinationSaved2
                = productOptionCombinationRepository.save(productOptionCombination2);

        assertTrue(productOptionCombinationRepository.findById(productOptionCombinationSaved.getId()).isPresent());
        assertTrue(productOptionCombinationRepository.findById(productOptionCombinationSaved2.getId()).isPresent());

        productService.deleteProduct(afterProduct.getId());
        Optional<Product> result = productRepository.findById(afterProduct.getId());

        assertTrue(result.isPresent());
        assertFalse(result.get().isPublished());
        assertFalse(productOptionCombinationRepository.findById(productOptionCombinationSaved.getId()).isPresent());
        assertFalse(productOptionCombinationRepository.findById(productOptionCombinationSaved2.getId()).isPresent());
    }

    @Test
    void deleteProduct_givenNoOption_noDeleteAll() {
        Product parent = productRepository.findAll().getFirst();
        Product product = getVariant(parent);
        Product afterProduct = productRepository.save(product);

        productService.deleteProduct(afterProduct.getId());
        Optional<Product> result = productRepository.findById(afterProduct.getId());

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
        Double startPrice = 1.0;
        Double endPrice = 10.0;
        String productName = "product2";
        ProductsGetVm result = productService.getProductsByMultiQuery(pageNo,
            pageSize, productName, category2.getSlug(), startPrice, endPrice);

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

    @Test
    void getProductsByCategoryIds_WhenFindAllByCategoryIds_ThenSuccess() {
        List<Long> categoryIds = List.of(1L, 2L);
        List<ProductListVm> actualResponse = productService.getProductByCategoryIds(categoryIds);
        assertEquals(0, actualResponse.size());
    }

    @Test
    void getProductsByBrandIds_WhenFindAllByBrandIds_ThenSuccess() {
        List<Long> brandIds = List.of(brand1.getId(), brand2.getId());
        List<ProductListVm> actualResponse = productService.getProductByBrandIds(brandIds);
        assertEquals(10, actualResponse.size());
        assertEquals("product1", actualResponse.getFirst().name());
        assertEquals("slug1", actualResponse.getFirst().slug());
    }


    @Test
    void testGetLatestProducts_WhenHasListProductListVm_returnListProductListVm() {

        List<Product>  actualResponse = productRepository.findAll();
        assertEquals(10, actualResponse.size());
        actualResponse.sort((Comparator.comparing(AbstractAuditEntity::getCreatedOn).reversed()));

        List<ProductListVm>  newResponse = productService.getLatestProducts(5);
        assertEquals(5, newResponse.size());
        IntStream.range(0, 5).forEach(i ->
            assertEquals(actualResponse.get(i).getName(), newResponse.get(i).name())
        );
    }

    @Test
    void testGetLatestProducts_WhenCountLessThen1_returnEmpty() {
        List<ProductListVm> newResponse = productService.getLatestProducts(-1);
        assertEquals(0, newResponse.size());
    }

    @Test
    void testGetLatestProducts_WhenCountIs0_returnEmpty() {
        List<ProductListVm> newResponse = productService.getLatestProducts(0);
        assertEquals(0, newResponse.size());
    }

    @Test
    void testGetLatestProducts_WhenProductsEmpty_returnEmpty() {
        tearDown();
        List<ProductListVm> newResponse = productService.getLatestProducts(5);
        assertEquals(0, newResponse.size());
    }

}