package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.model.ProductRelated;
import com.yas.product.model.enumeration.FilterExistInWhSelection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MediaService mediaService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private ProductOptionValueRepository productOptionValueRepository;

    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @Mock
    private ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductPostVm productPostVm;
    private ProductPutVm productPutVm;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .slug("test-product")
                .sku("TEST-SKU")
                .price(100.0)
                .isPublished(true)
                .isVisibleIndividually(true)
                .isAllowedToOrder(true)
                .isFeatured(false)
                .thumbnailMediaId(1L)
                .productCategories(new ArrayList<>())
                .productImages(new ArrayList<>())
                .build();

        productPostVm = new ProductPostVm(
                "Test Product", "test-product", null, null,
                "Short Desc", "Desc", "Spec", "TEST-SKU", "GTIN123",
                10.0, null, 10.0, 5.0, 5.0,
                100.0, true, true, false, true, false,
                "Meta Title", "Meta Keyword", "Meta Desc",
                1L, null, Collections.emptyList(), Collections.emptyList(), null, null, null);

        productPutVm = new ProductPutVm(
                "Updated Product", "updated-product", 150.0, true, true, false, true, false,
                null, null, "Short Desc", "Desc", "Spec", "TEST-SKU-2", "GTIN1234",
                10.0, null, 10.0, 5.0, 5.0,
                "Meta Title", "Meta Keyword", "Meta Desc",
                1L, null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null);
    }

    @Test
    void createProduct_Success() {
        // Arrange
        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("TEST-SKU")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("GTIN123")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductGetDetailVm result = productService.createProduct(productPostVm);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.name());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_ThrowsDuplicatedException_WhenSlugExists() {
        // Arrange
        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(DuplicatedException.class, () -> productService.createProduct(productPostVm));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createProduct_ThrowsBadRequestException_WhenLengthLessThanWidth() {
        // Arrange
        ProductPostVm invalidVm = new ProductPostVm(
                "Test Product", "test-product", null, null,
                "Short Desc", "Desc", "Spec", "TEST-SKU", "GTIN123",
                10.0, null, 2.0, 5.0, 5.0,
                100.0, true, true, false, true, false,
                "Meta Title", "Meta Keyword", "Meta Desc",
                1L, null, Collections.emptyList(), Collections.emptyList(), null, null, null);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.createProduct(invalidVm));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        NoFileMediaVm mediaVm = new NoFileMediaVm(1L, "test", "test", "test", "url");
        when(mediaService.getMedia(1L)).thenReturn(mediaVm);

        // Act
        ProductDetailVm result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.name());
        assertEquals("url", result.thumbnailMedia().url());
    }

    @Test
    void getProductById_ThrowsNotFoundException_WhenNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getProductsWithFilter_Success() {
        // Arrange
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.getProductsWithFilter(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(productPage);

        // Act
        ProductListGetVm result = productService.getProductsWithFilter(0, 10, "Test", "Brand");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.productContent().size());
        assertEquals("Test Product", result.productContent().get(0).name());
    }

    @Test
    void getLatestProducts_Success() {
        // Arrange
        when(productRepository.getLatestProducts(any(Pageable.class)))
                .thenReturn(List.of(product));

        // Act
        List<ProductListVm> result = productService.getLatestProducts(10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).name());
    }

    @Test
    void getLatestProducts_ReturnsEmpty_WhenCountIsZeroOrLess() {
        // Act
        List<ProductListVm> result = productService.getLatestProducts(0);

        // Assert
        assertTrue(result.isEmpty());
        verify(productRepository, never()).getLatestProducts(any());
    }

    @Test
    void updateProduct_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findBySlugAndIsPublishedTrue("updated-product")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("TEST-SKU-2")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("GTIN1234")).thenReturn(Optional.empty());
        when(productCategoryRepository.findAllByProductId(1L)).thenReturn(new ArrayList<>());
        ProductOption productOption = new ProductOption();
        productOption.setId(1L);
        when(productOptionRepository.findAllByIdIn(any())).thenReturn(List.of(productOption));
        when(productOptionValueRepository.saveAll(any())).thenReturn(new ArrayList<>());

        // Act
        productService.updateProduct(1L, productPutVm);

        // Assert
        verify(productRepository).saveAll(any());
        assertEquals("updated-product", product.getSlug());
        assertEquals(150.0, product.getPrice());
    }

    @Test
    void updateProduct_ThrowsNotFoundException_WhenNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> productService.updateProduct(1L, productPutVm));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductsByBrand_Success() {
        // Arrange
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Brand");
        brand.setSlug("brand-slug");
        when(brandRepository.findBySlug("brand-slug")).thenReturn(Optional.of(brand));
        when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand)).thenReturn(List.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "test", "test", "test", "url"));

        // Act
        var result = productService.getProductsByBrand("brand-slug");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).name());
    }

    @Test
    void getProductsFromCategory_Success() {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Category");
        category.setSlug("category-slug");
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(category);
        productCategory.setProduct(product);

        when(categoryRepository.findBySlug("category-slug")).thenReturn(Optional.of(category));
        Page<ProductCategory> page = new PageImpl<>(List.of(productCategory));
        when(productCategoryRepository.findAllByCategory(any(Pageable.class), eq(category))).thenReturn(page);
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "test", "test", "test", "url"));

        // Act
        var result = productService.getProductsFromCategory(0, 10, "category-slug");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.productContent().size());
        assertEquals("Test Product", result.productContent().get(0).name());
    }

    @Test
    void deleteProduct_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        productService.deleteProduct(1L);

        // Assert
        assertFalse(product.isPublished());
        verify(productRepository).save(product);
    }

    @Test
    void deleteProduct_SuccessWithCombinations() {
        // Arrange
        Product parent = new Product();
        product.setParent(parent);
        ProductOptionCombination combination = new ProductOptionCombination();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(product)).thenReturn(List.of(combination));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        productService.deleteProduct(1L);

        // Assert
        assertFalse(product.isPublished());
        verify(productOptionCombinationRepository).deleteAll(List.of(combination));
        verify(productRepository).save(product);
    }

    @Test
    void deleteProduct_ThrowsNotFoundException_WhenNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductSlug_ReturnsSlug_WhenNoParent() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductSlug(1L);
        assertEquals("test-product", result.slug());
        assertEquals(null, result.productVariantId());
    }

    @Test
    void getProductSlug_ReturnsParentSlug_WhenHasParent() {
        Product parent = Product.builder().id(2L).slug("parent-slug").build();
        product.setParent(parent);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductSlug(1L);
        assertEquals("parent-slug", result.slug());
        assertEquals(1L, result.productVariantId());
    }

    @Test
    void getProductSlug_ThrowsNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductSlug(1L));
    }

    @Test
    void getProductByIds_Success() {
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
        var result = productService.getProductByIds(List.of(1L));
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).name());
    }

    @Test
    void getProductByCategoryIds_Success() {
        when(productRepository.findByCategoryIdsIn(List.of(1L))).thenReturn(List.of(product));
        var result = productService.getProductByCategoryIds(List.of(1L));
        assertEquals(1, result.size());
    }

    @Test
    void getProductByBrandIds_Success() {
        when(productRepository.findByBrandIdsIn(List.of(1L))).thenReturn(List.of(product));
        var result = productService.getProductByBrandIds(List.of(1L));
        assertEquals(1, result.size());
    }

    @Test
    void getProductsByMultiQuery_Success() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(
                anyString(), anyString(), any(), any(), any(Pageable.class))).thenReturn(page);
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));
        var result = productService.getProductsByMultiQuery(0, 10, "Test", "cat", 0.0, 200.0);
        assertNotNull(result);
        assertEquals(1, result.productContent().size());
    }

    @Test
    void getListFeaturedProducts_Success() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(page);
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));
        var result = productService.getListFeaturedProducts(0, 10);
        assertNotNull(result);
        assertEquals(1, result.productList().size());
    }

    @Test
    void getFeaturedProductsById_Success() {
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));
        var result = productService.getFeaturedProductsById(List.of(1L));
        assertEquals(1, result.size());
        assertEquals("url", result.get(0).thumbnailUrl());
    }

    @Test
    void getFeaturedProductsById_FallbackToParent() {
        Product parent = Product.builder().id(2L).thumbnailMediaId(2L).build();
        Product child = Product.builder().id(3L).name("Child").slug("child").price(50.0)
                .thumbnailMediaId(3L).parent(parent).build();
        when(productRepository.findAllByIdIn(List.of(3L))).thenReturn(List.of(child));
        when(mediaService.getMedia(3L)).thenReturn(new NoFileMediaVm(3L, "", "", "", ""));
        when(productRepository.findById(2L)).thenReturn(Optional.of(parent));
        when(mediaService.getMedia(2L)).thenReturn(new NoFileMediaVm(2L, "", "", "", "parent-url"));
        var result = productService.getFeaturedProductsById(List.of(3L));
        assertEquals(1, result.size());
    }

    @Test
    void exportProducts_Success() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Brand");
        product.setBrand(brand);
        when(productRepository.getExportingProducts(anyString(), anyString())).thenReturn(List.of(product));
        var result = productService.exportProducts("Test", "Brand");
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).name());
    }

    @Test
    void getProductEsDetailById_Success() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Brand");
        product.setBrand(brand);
        product.setProductCategories(new ArrayList<>());
        product.setAttributeValues(new ArrayList<>());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductEsDetailById(1L);
        assertNotNull(result);
        assertEquals("Test Product", result.name());
        assertEquals("Brand", result.brand());
    }

    @Test
    void getProductEsDetailById_NoBrand() {
        product.setProductCategories(new ArrayList<>());
        product.setAttributeValues(new ArrayList<>());
        product.setThumbnailMediaId(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductEsDetailById(1L);
        assertNotNull(result);
        assertEquals(null, result.brand());
        assertEquals(null, result.thumbnailMediaId());
    }

    @Test
    void getProductEsDetailById_ThrowsNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductEsDetailById(1L));
    }

    @Test
    void getRelatedProductsBackoffice_Success() {
        Product relatedProduct = Product.builder().id(2L).name("Related").slug("related")
                .price(50.0).isPublished(true).isAllowedToOrder(true).isFeatured(false)
                .isVisibleIndividually(true).build();
        ProductRelated pr = ProductRelated.builder().product(product).relatedProduct(relatedProduct).build();
        product.setRelatedProducts(List.of(pr));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getRelatedProductsBackoffice(1L);
        assertEquals(1, result.size());
        assertEquals("Related", result.get(0).name());
    }

    @Test
    void getRelatedProductsStorefront_Success() {
        Product relatedProduct = Product.builder().id(2L).name("Related").slug("related")
                .price(50.0).isPublished(true).thumbnailMediaId(2L).build();
        ProductRelated pr = ProductRelated.builder().product(product).relatedProduct(relatedProduct).build();
        Page<ProductRelated> page = new PageImpl<>(List.of(pr));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRelatedRepository.findAllByProduct(any(Product.class), any(Pageable.class))).thenReturn(page);
        when(mediaService.getMedia(2L)).thenReturn(new NoFileMediaVm(2L, "", "", "", "url"));
        var result = productService.getRelatedProductsStorefront(1L, 0, 10);
        assertNotNull(result);
    }

    @Test
    void updateProductQuantity_Success() {
        product.setStockQuantity(100L);
        ProductQuantityPostVm qvm = new ProductQuantityPostVm(1L, 50L);
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
        productService.updateProductQuantity(List.of(qvm));
        assertEquals(50L, product.getStockQuantity());
        verify(productRepository).saveAll(any());
    }

    @Test
    void subtractStockQuantity_Success() {
        product.setStockQuantity(100L);
        product.setStockTrackingEnabled(true);
        ProductQuantityPutVm qvm = new ProductQuantityPutVm(1L, 30L);
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        productService.subtractStockQuantity(List.of(qvm));
        assertEquals(70L, product.getStockQuantity());
    }

    @Test
    void subtractStockQuantity_DoesNotGoBelowZero() {
        product.setStockQuantity(10L);
        product.setStockTrackingEnabled(true);
        ProductQuantityPutVm qvm = new ProductQuantityPutVm(1L, 20L);
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        productService.subtractStockQuantity(List.of(qvm));
        assertEquals(0L, product.getStockQuantity());
    }

    @Test
    void restoreStockQuantity_Success() {
        product.setStockQuantity(50L);
        product.setStockTrackingEnabled(true);
        ProductQuantityPutVm qvm = new ProductQuantityPutVm(1L, 30L);
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        productService.restoreStockQuantity(List.of(qvm));
        assertEquals(80L, product.getStockQuantity());
    }

    @Test
    void getProductsForWarehouse_Success() {
        when(productRepository.findProductForWarehouse(anyString(), anyString(), any(), anyString()))
                .thenReturn(List.of(product));
        var result = productService.getProductsForWarehouse("Test", "SKU", List.of(1L),
                FilterExistInWhSelection.ALL);
        assertEquals(1, result.size());
    }

    @Test
    void getProductDetail_Success() {
        product.setProductCategories(new ArrayList<>());
        product.setProductImages(new ArrayList<>());
        product.setAttributeValues(new ArrayList<>());
        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "thumb-url"));
        var result = productService.getProductDetail("test-product");
        assertNotNull(result);
        assertEquals("Test Product", result.name());
        assertEquals("thumb-url", result.thumbnailMediaUrl());
    }

    @Test
    void getProductDetail_WithBrand() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("TestBrand");
        product.setBrand(brand);
        product.setProductCategories(new ArrayList<>());
        product.setProductImages(new ArrayList<>());
        product.setAttributeValues(new ArrayList<>());
        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));
        var result = productService.getProductDetail("test-product");
        assertEquals("TestBrand", result.brandName());
    }

    @Test
    void getProductDetail_WithImages() {
        ProductImage img = ProductImage.builder().imageId(5L).product(product).build();
        product.setProductImages(List.of(img));
        product.setProductCategories(new ArrayList<>());
        product.setAttributeValues(new ArrayList<>());
        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.of(product));
        when(mediaService.getMedia(anyLong())).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));
        var result = productService.getProductDetail("test-product");
        assertEquals(1, result.productImageMediaUrls().size());
    }

    @Test
    void getProductDetail_ThrowsNotFound() {
        when(productRepository.findBySlugAndIsPublishedTrue("bad")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductDetail("bad"));
    }

    @Test
    void getProductVariationsByParentId_NoOptions() {
        product.setHasOptions(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductVariationsByParentId(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void getProductVariationsByParentId_WithOptions() {
        Product variation = Product.builder().id(2L).name("Var").slug("var").sku("V1")
                .gtin("G1").price(50.0).isPublished(true).thumbnailMediaId(2L)
                .productImages(new ArrayList<>()).build();
        product.setHasOptions(true);
        product.setProducts(List.of(variation));
        ProductOption po = new ProductOption();
        po.setId(10L);
        ProductOptionCombination combo = ProductOptionCombination.builder()
                .product(variation).productOption(po).value("Red").build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of(combo));
        when(mediaService.getMedia(2L)).thenReturn(new NoFileMediaVm(2L, "", "", "", "var-url"));
        var result = productService.getProductVariationsByParentId(1L);
        assertEquals(1, result.size());
        assertEquals("Var", result.get(0).name());
    }

    @Test
    void getProductVariationsByParentId_ThrowsNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductVariationsByParentId(1L));
    }

    @Test
    void getProductsByBrand_ThrowsNotFound() {
        when(brandRepository.findBySlug("bad")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductsByBrand("bad"));
    }

    @Test
    void getProductsFromCategory_ThrowsNotFound() {
        when(categoryRepository.findBySlug("bad")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductsFromCategory(0, 10, "bad"));
    }

    @Test
    void getLatestProducts_ReturnsEmpty_WhenRepositoryReturnsEmpty() {
        when(productRepository.getLatestProducts(any(Pageable.class))).thenReturn(Collections.emptyList());
        var result = productService.getLatestProducts(5);
        assertTrue(result.isEmpty());
    }

    @Test
    void setProductImages_EmptyList() {
        var result = productService.setProductImages(Collections.emptyList(), product);
        assertTrue(result.isEmpty());
        verify(productImageRepository).deleteByProductId(product.getId());
    }

    @Test
    void setProductImages_NullList() {
        var result = productService.setProductImages(null, product);
        assertTrue(result.isEmpty());
    }

    @Test
    void setProductImages_NewImages_WhenProductHasNoImages() {
        product.setProductImages(null);
        var result = productService.setProductImages(List.of(10L, 11L), product);
        assertEquals(2, result.size());
    }

    @Test
    void createProduct_ThrowsDuplicated_WhenSkuExists() {
        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("TEST-SKU")).thenReturn(Optional.of(product));
        assertThrows(DuplicatedException.class, () -> productService.createProduct(productPostVm));
    }

    @Test
    void createProduct_ThrowsDuplicated_WhenGtinExists() {
        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("GTIN123")).thenReturn(Optional.of(product));
        assertThrows(DuplicatedException.class, () -> productService.createProduct(productPostVm));
    }

    @Test
    void createProduct_WithBrand() {
        Brand brand = new Brand();
        brand.setId(5L);
        brand.setName("B");
        ProductPostVm vm = new ProductPostVm(
                "P", "p-slug", 5L, null,
                "s", "d", "sp", "SK1", "GT1",
                10.0, null, 10.0, 5.0, 5.0,
                100.0, true, true, false, true, false,
                "mt", "mk", "md",
                1L, null, Collections.emptyList(), Collections.emptyList(), null, null, null);
        when(productRepository.findBySlugAndIsPublishedTrue("p-slug")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("SK1")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("GT1")).thenReturn(Optional.empty());
        when(brandRepository.findById(5L)).thenReturn(Optional.of(brand));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        var result = productService.createProduct(vm);
        assertNotNull(result);
        verify(brandRepository).findById(5L);
    }

    @Test
    void getProductById_WithImages() {
        ProductImage img = ProductImage.builder().imageId(5L).product(product).build();
        product.setProductImages(List.of(img));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(anyLong())).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));
        var result = productService.getProductById(1L);
        assertNotNull(result);
        assertEquals(1, result.productImageMedias().size());
    }

    @Test
    void getProductById_WithBrandAndCategories() {
        Brand brand = new Brand();
        brand.setId(1L);
        product.setBrand(brand);
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("Cat");
        ProductCategory pc = new ProductCategory();
        pc.setCategory(cat);
        pc.setProduct(product);
        product.setProductCategories(List.of(pc));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(anyLong())).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));
        var result = productService.getProductById(1L);
        assertEquals(1L, result.brandId());
        assertEquals(1, result.categories().size());
    }

    @Test
    void getProductById_NoThumbnail() {
        product.setThumbnailMediaId(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductById(1L);
        assertNotNull(result);
        assertEquals(null, result.thumbnailMedia());
    }
}
