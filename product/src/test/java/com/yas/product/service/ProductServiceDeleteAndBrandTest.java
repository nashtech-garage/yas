package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
class ProductServiceDeleteAndBrandTest {

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

    // ========== deleteProduct ==========

    @Test
    void deleteProduct_whenProductExists_thenMarkAsUnpublished() {
        product.setParent(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        assertThat(product.isPublished()).isFalse();
        verify(productRepository).save(product);
    }

    @Test
    void deleteProduct_whenProductNotFound_thenThrowNotFoundException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.deleteProduct(999L));
    }

    // ========== getProductsByBrand ==========

    @Test
    void getProductsByBrand_whenBrandNotFound_thenThrowNotFoundException() {
        when(brandRepository.findBySlug("non-existent")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductsByBrand("non-existent"));
    }

    @Test
    void getProductsByBrand_whenBrandExists_thenReturnThumbnailList() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Brand A");

        product.setThumbnailMediaId(10L);
        when(brandRepository.findBySlug("brand-a")).thenReturn(Optional.of(brand));
        when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand)).thenReturn(List.of(product));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://img.url"));

        var result = productService.getProductsByBrand("brand-a");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Test Product");
    }

}
