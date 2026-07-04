package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
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
import com.yas.product.viewmodel.product.ProductPutVm;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceUpdateTest {

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


    // ========== updateProduct ==========

    @Test
    void updateProduct_whenProductNotFound_thenThrowNotFoundException() {
        ProductPutVm putVm = new ProductPutVm(
            "Updated Product", "updated-slug", 100.0, true, true, true, true, true, 
            null, Collections.emptyList(), "Short desc", "Desc", "Spec", "SKU-UPDATED", "GTIN-UPDATED", 
            1.0, null, 10.0, 5.0, 5.0, "Meta title", "Meta keyword", "Meta desc", 
            null, Collections.emptyList(), Collections.emptyList(), 
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null
        );
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.updateProduct(999L, putVm));
    }

    @Test
    void updateProduct_whenValidInput_thenUpdatesSuccessfully() {
        ProductPutVm putVm = new ProductPutVm(
            "Updated Product", "updated-slug", 100.0, true, true, true, true, true, 
            null, Collections.emptyList(), "Short desc", "Desc", "Spec", "SKU-UPDATED", "GTIN-UPDATED", 
            1.0, null, 10.0, 5.0, 5.0, "Meta title", "Meta keyword", "Meta desc", 
            null, Collections.emptyList(), Collections.emptyList(), 
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findBySlugAndIsPublishedTrue("updated-slug")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("GTIN-UPDATED")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("SKU-UPDATED")).thenReturn(Optional.empty());
        
        com.yas.product.model.ProductOption option = new com.yas.product.model.ProductOption();
        option.setId(1L);
        when(productOptionRepository.findAllByIdIn(any())).thenReturn(List.of(option));

        productService.updateProduct(1L, putVm);

        assertThat(product.getName()).isEqualTo("Updated Product");
        assertThat(product.getSlug()).isEqualTo("updated-slug");
    }

}
