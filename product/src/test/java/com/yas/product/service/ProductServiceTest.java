package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductRelated;
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
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductDetailGetVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductVariationPostVm;
import com.yas.product.viewmodel.product.ProductVariationPutVm;
import com.yas.product.viewmodel.product.ProductVariationGetVm;
import com.yas.product.viewmodel.product.ProductExportingDetailVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductEsDetailVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePutVm;
import java.util.Map;

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
    private Brand brand;
    private Category category;
    private NoFileMediaVm noFileMediaVm;

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(1L);
        brand.setName("Test Brand");
        brand.setSlug("test-brand");

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setSlug("test-category");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setSlug("test-product");
        product.setBrand(brand);
        product.setThumbnailMediaId(1L);
        product.setPrice(100.0);
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setProduct(product);
        productCategory.setCategory(category);
        
        List<ProductCategory> productCategories = new ArrayList<>();
        productCategories.add(productCategory);
        product.setProductCategories(productCategories);

        ProductImage productImage = new ProductImage();
        productImage.setImageId(2L);
        productImage.setProduct(product);
        
        List<ProductImage> productImages = new ArrayList<>();
        productImages.add(productImage);
        product.setProductImages(productImages);
        
        product.setProducts(new ArrayList<>());
        product.setRelatedProducts(new ArrayList<>());

        noFileMediaVm = new NoFileMediaVm(1L, "caption", "fileName", "mediaType", "url");
    }

    @Test
    void getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(noFileMediaVm);
        when(mediaService.getMedia(2L)).thenReturn(noFileMediaVm);

        ProductDetailVm result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test Product", result.name());
        assertEquals("url", result.thumbnailMedia().url());
        assertEquals(1, result.productImageMedias().size());
        assertEquals(1, result.categories().size());
        assertEquals(1L, result.brandId());
    }

    @Test
    void getProductById_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getProductsWithFilter_Success() {
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.getProductsWithFilter(anyString(), anyString(), any(Pageable.class))).thenReturn(page);

        ProductListGetVm result = productService.getProductsWithFilter(0, 10, "Test", "Brand");

        assertNotNull(result);
        assertEquals(1, result.productContent().size());
        assertEquals("Test Product", result.productContent().get(0).name());
    }

    @Test
    void getLatestProducts_Success() {
        when(productRepository.getLatestProducts(any(Pageable.class))).thenReturn(List.of(product));
        
        List<ProductListVm> result = productService.getLatestProducts(5);
        
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).name());
    }

    @Test
    void getLatestProducts_InvalidCount() {
        List<ProductListVm> result = productService.getLatestProducts(0);
        assertTrue(result.isEmpty());
    }

    @Test
    void getProductsByBrand_Success() {
        when(brandRepository.findBySlug("test-brand")).thenReturn(Optional.of(brand));
        when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand)).thenReturn(List.of(product));
        when(mediaService.getMedia(1L)).thenReturn(noFileMediaVm);

        List<ProductThumbnailVm> result = productService.getProductsByBrand("test-brand");

        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).name());
    }

    @Test
    void getProductsByBrand_NotFound() {
        when(brandRepository.findBySlug("unknown")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductsByBrand("unknown"));
    }

    @Test
    void getProductsFromCategory_Success() {
        when(categoryRepository.findBySlug("test-category")).thenReturn(Optional.of(category));
        ProductCategory productCategory = new ProductCategory();
        productCategory.setProduct(product);
        Page<ProductCategory> page = new PageImpl<>(List.of(productCategory), PageRequest.of(0, 10), 1);
        when(productCategoryRepository.findAllByCategory(any(Pageable.class), eq(category))).thenReturn(page);
        when(mediaService.getMedia(1L)).thenReturn(noFileMediaVm);

        ProductListGetFromCategoryVm result = productService.getProductsFromCategory(0, 10, "test-category");

        assertNotNull(result);
        assertEquals(1, result.productContent().size());
        assertEquals("Test Product", result.productContent().get(0).name());
    }

    @Test
    void getFeaturedProductsById_Success() {
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
        when(mediaService.getMedia(1L)).thenReturn(noFileMediaVm);

        List<ProductThumbnailGetVm> result = productService.getFeaturedProductsById(List.of(1L));

        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).name());
        assertEquals("url", result.get(0).thumbnailUrl());
    }

    @Test
    void getListFeaturedProducts_Success() {
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(page);
        when(mediaService.getMedia(1L)).thenReturn(noFileMediaVm);

        ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 10);

        assertNotNull(result);
        assertEquals(1, result.productList().size());
        assertEquals("Test Product", result.productList().get(0).name());
    }

    @Test
    void createProduct_Success() {
        ProductPostVm productPostVm = new ProductPostVm(
            "name", "slug", 1L, List.of(1L), "shortDesc", "desc", "spec", "sku", "gtin",
            1.0, null, 1.0, 1.0, 1.0, 100.0, true, true, true, true, true,
            "meta", "meta", "meta", 1L, List.of(2L), List.of(), List.of(), List.of(), List.of(), null
        );

        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        lenient().when(productRepository.save(any())).thenReturn(product);

        ProductGetDetailVm result = productService.createProduct(productPostVm);

        assertNotNull(result);
        assertEquals("Test Product", result.name());
    }

    @Test
    void createProduct_WithVariations_Success() {
        Map<Long, String> optionValuesByOptionId = Map.of(1L, "Red");
        ProductVariationPostVm variationVm = new ProductVariationPostVm(
            "Variation 1", "var-1", "VAR-SKU", "VAR-GTIN", 150.0, 1L, List.of(2L), optionValuesByOptionId
        );
        ProductOptionValuePostVm optionValueVm = new ProductOptionValuePostVm(1L, "text", 1, List.of("Red"));
        
        ProductPostVm productPostVm = new ProductPostVm(
            "name", "slug", 1L, List.of(1L), "shortDesc", "desc", "spec", "sku", "gtin",
            1.0, null, 1.0, 1.0, 1.0, 100.0, true, true, true, true, true,
            "meta", "meta", "meta", 1L, List.of(2L), 
            List.of(variationVm), 
            List.of(optionValueVm), 
            List.of(), 
            List.of(), null
        );

        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        lenient().when(productRepository.save(any())).thenReturn(product);
        
        com.yas.product.model.ProductOption productOption = new com.yas.product.model.ProductOption();
        productOption.setId(1L);
        productOption.setName("Color");
        when(productOptionRepository.findAllByIdIn(any())).thenReturn(List.of(productOption));
        
        com.yas.product.model.ProductOptionValue pov = new com.yas.product.model.ProductOptionValue();
        pov.setValue("Red");
        pov.setProductOption(productOption);
        pov.setDisplayOrder(1);
        lenient().when(productOptionValueRepository.saveAll(any())).thenReturn(List.of(pov));
        
        Product variationProduct = new Product();
        variationProduct.setSlug("var-1");
        when(productRepository.saveAll(any())).thenReturn(List.of(variationProduct));

        ProductGetDetailVm result = productService.createProduct(productPostVm);

        assertNotNull(result);
        assertEquals("Test Product", result.name());
    }

    @Test
    void updateProduct_Success() {
        ProductPutVm productPutVm = new ProductPutVm(
            "name", "slug", 100.0, true, true, true, true, true,
            1L, List.of(1L), "shortDesc", "desc", "spec", "sku", "gtin",
            1.0, null, 1.0, 1.0, 1.0, "meta", "meta", "meta", 
            1L, List.of(2L), List.of(), List.of(), List.of(), List.of(), null
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productCategoryRepository.findAllByProductId(1L)).thenReturn(List.of());
        when(productOptionRepository.findAllByIdIn(any())).thenReturn(List.of(new com.yas.product.model.ProductOption()));

        productService.updateProduct(1L, productPutVm);
        
        assertEquals("name", product.getName());
    }

    @Test
    void updateProduct_WithVariations_Success() {
        Map<Long, String> optionValuesByOptionId = Map.of(1L, "Blue");
        ProductVariationPutVm variationVm = new ProductVariationPutVm(
            null, "Variation 2", "var-2", "VAR-SKU-2", "VAR-GTIN-2", 200.0, 1L, List.of(2L), optionValuesByOptionId
        );
        ProductOptionValuePutVm optionValueVm = new ProductOptionValuePutVm(1L, "text", 1, List.of("Blue"));
        
        ProductPutVm productPutVm = new ProductPutVm(
            "name", "slug", 100.0, true, true, true, true, true,
            1L, List.of(1L), "shortDesc", "desc", "spec", "sku", "gtin",
            1.0, null, 1.0, 1.0, 1.0, "meta", "meta", "meta", 
            1L, List.of(2L), 
            List.of(variationVm), 
            List.of(optionValueVm), 
            List.of(), 
            List.of(), null
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productCategoryRepository.findAllByProductId(1L)).thenReturn(List.of());
        
        com.yas.product.model.ProductOption productOption = new com.yas.product.model.ProductOption();
        productOption.setId(1L);
        productOption.setName("Color");
        lenient().when(productOptionRepository.findAllByIdIn(any())).thenReturn(List.of(productOption));

        com.yas.product.model.ProductOptionValue pov = new com.yas.product.model.ProductOptionValue();
        pov.setValue("Blue");
        pov.setProductOption(productOption);
        pov.setDisplayOrder(1);
        lenient().when(productOptionValueRepository.saveAll(any())).thenReturn(List.of(pov));
        
        Product variationProduct = new Product();
        variationProduct.setSlug("var-2");
        when(productRepository.saveAll(any())).thenReturn(List.of(variationProduct));

        productService.updateProduct(1L, productPutVm);
        
        assertEquals("name", product.getName());
    }
    
    @Test
    void getProductDetail_Success() {
        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(noFileMediaVm);
        when(mediaService.getMedia(2L)).thenReturn(noFileMediaVm);
        
        ProductDetailGetVm result = productService.getProductDetail("test-product");
        
        assertNotNull(result);
        assertEquals("Test Product", result.name());
    }

    @Test
    void getProductDetail_NotFound() {
        when(productRepository.findBySlugAndIsPublishedTrue("unknown")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductDetail("unknown"));
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        productService.deleteProduct(1L);
        assertFalse(product.isPublished());
    }

    @Test
    void deleteProduct_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void getProductsByMultiQuery_Success() {
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(any(), any(), any(), any(), any()))
            .thenReturn(page);
        when(mediaService.getMedia(1L)).thenReturn(noFileMediaVm);

        ProductsGetVm result = productService.getProductsByMultiQuery(0, 10, "Test", "category", 0.0, 100.0);
        
        assertNotNull(result);
        assertEquals(1, result.productContent().size());
        assertEquals("Test Product", result.productContent().get(0).name());
    }

    @Test
    void getProductVariationsByParentId_Success() {
        product.setHasOptions(true);
        Product child = new Product();
        child.setId(2L);
        child.setPublished(true);
        child.setThumbnailMediaId(1L);
        child.setProductImages(List.of());
        product.setProducts(List.of(child));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(any())).thenReturn(List.of());
        when(mediaService.getMedia(1L)).thenReturn(noFileMediaVm);
        
        var result = productService.getProductVariationsByParentId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void exportProducts_Success() {
        when(productRepository.getExportingProducts(any(), any())).thenReturn(List.of(product));
        var result = productService.exportProducts("Test", "Brand");
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).name());
    }

    @Test
    void getProductSlug_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductSlug(1L);
        assertEquals("test-product", result.slug());
    }

    @Test
    void getProductEsDetailById_Success() {
        product.setProductCategories(List.of());
        product.setAttributeValues(List.of());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getProductEsDetailById(1L);
        assertEquals("Test Product", result.name());
    }

    @Test
    void getRelatedProductsBackoffice_Success() {
        Product related = new Product();
        related.setId(2L);
        ProductRelated pr = new ProductRelated();
        pr.setRelatedProduct(related);
        product.setRelatedProducts(List.of(pr));
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productService.getRelatedProductsBackoffice(1L);
        assertEquals(1, result.size());
    }
}
