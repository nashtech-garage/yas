package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import com.yas.product.viewmodel.product.ProductListVm;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceGetByIdsTest {

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

    // ========== getProductByIds ==========

    @Test
    void getProductByIds_whenValidIds_thenReturnMappedList() {
        when(productRepository.findAllByIdIn(List.of(1L, 2L))).thenReturn(List.of(product));

        List<ProductListVm> result = productService.getProductByIds(List.of(1L, 2L));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Test Product");
    }

    @Test
    void getProductByIds_whenNoProductsFound_thenReturnEmptyList() {
        when(productRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());
        List<ProductListVm> result = productService.getProductByIds(List.of(99L));
        assertThat(result).isEmpty();
    }

    // ========== getProductByCategoryIds ==========

    @Test
    void getProductByCategoryIds_whenCategoryHasProducts_thenReturnList() {
        when(productRepository.findByCategoryIdsIn(List.of(1L))).thenReturn(List.of(product));

        List<ProductListVm> result = productService.getProductByCategoryIds(List.of(1L));

        assertThat(result).hasSize(1);
    }

    @Test
    void getProductByCategoryIds_whenNoCategoryMatch_thenReturnEmptyList() {
        when(productRepository.findByCategoryIdsIn(any())).thenReturn(Collections.emptyList());
        List<ProductListVm> result = productService.getProductByCategoryIds(List.of(99L));
        assertThat(result).isEmpty();
    }

    // ========== getProductByBrandIds ==========

    @Test
    void getProductByBrandIds_whenBrandHasProducts_thenReturnList() {
        when(productRepository.findByBrandIdsIn(List.of(1L))).thenReturn(List.of(product));

        List<ProductListVm> result = productService.getProductByBrandIds(List.of(1L));

        assertThat(result).hasSize(1);
    }

    @Test
    void getProductByBrandIds_whenNoBrandMatch_thenReturnEmptyList() {
        when(productRepository.findByBrandIdsIn(any())).thenReturn(Collections.emptyList());
        List<ProductListVm> result = productService.getProductByBrandIds(List.of(99L));
        assertThat(result).isEmpty();
    }

}
