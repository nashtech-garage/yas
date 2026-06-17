package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Product;
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
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductDetailGetVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import com.yas.product.viewmodel.product.ProductInfoVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.model.enumeration.FilterExistInWhSelection;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductEsDetailVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
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
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductServiceCreateTest {

        @Mock private ProductRepository productRepository;
    @Mock private MediaService mediaService;
    @Mock private BrandRepository brandRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductCategoryRepository productCategoryRepository;
    @Mock private ProductImageRepository productImageRepository;
    @Mock private ProductOptionRepository productOptionRepository;
    @Mock private ProductOptionValueRepository productOptionValueRepository;
    @Mock private ProductOptionCombinationRepository productOptionCombinationRepository;
    @Mock private ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setSlug("test-product");
        product.setSku("SKU-001");
        product.setPublished(true);
        product.setProductCategories(Collections.emptyList());
        product.setAttributeValues(Collections.emptyList());
        product.setProductImages(Collections.emptyList());
        product.setProducts(Collections.emptyList());
        product.setRelatedProducts(Collections.emptyList());
    }

    // ========== createProduct ==========

    @Test
    void createProduct_whenValidInput_thenReturnsProductGetDetailVm() {
        ProductPostVm productPostVm = new ProductPostVm(
            "New Product", "new-product", null, Collections.emptyList(), 
            "Short desc", "Desc", "Spec", "SKU-NEW", "GTIN-NEW", 
            1.0, null, 10.0, 5.0, 5.0, 100.0, 
            true, true, true, true, true, 
            "Meta title", "Meta keyword", "Meta desc", 
            null, Collections.emptyList(), Collections.emptyList(), 
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null
        );

        when(productRepository.findBySlugAndIsPublishedTrue("new-product")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("GTIN-NEW")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("SKU-NEW")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductGetDetailVm result = productService.createProduct(productPostVm);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Test Product"); // mapped from mock 'product'
    }

    @Test
    void createProduct_whenLengthLessThanWidth_thenThrowsBadRequestException() {
        ProductPostVm productPostVm = new ProductPostVm(
            "New Product", "new-product", null, Collections.emptyList(), 
            "Short desc", "Desc", "Spec", "SKU-NEW", "GTIN-NEW", 
            1.0, null, 5.0, 10.0, 5.0, 100.0, // length=5.0 < width=10.0
            true, true, true, true, true, 
            "Meta title", "Meta keyword", "Meta desc", 
            null, Collections.emptyList(), Collections.emptyList(), 
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null
        );

        assertThrows(BadRequestException.class, () -> productService.createProduct(productPostVm));
    }

    @Test
    void createProduct_whenSlugExists_thenThrowsDuplicatedException() {
        ProductPostVm productPostVm = new ProductPostVm(
            "New Product", "existing-slug", null, Collections.emptyList(), 
            "Short desc", "Desc", "Spec", "SKU-NEW", "GTIN-NEW", 
            1.0, null, 10.0, 5.0, 5.0, 100.0, 
            true, true, true, true, true, 
            "Meta title", "Meta keyword", "Meta desc", 
            null, Collections.emptyList(), Collections.emptyList(), 
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null
        );

        Product existingProduct = new Product();
        existingProduct.setId(99L);
        when(productRepository.findBySlugAndIsPublishedTrue("existing-slug")).thenReturn(Optional.of(existingProduct));

        assertThrows(DuplicatedException.class, () -> productService.createProduct(productPostVm));
    }

}
