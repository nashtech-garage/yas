package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
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
import com.yas.product.viewmodel.product.ProductListVm;
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
class ProductServiceRelationAndVariantTest {

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

    // ========== getRelatedProductsBackoffice ==========

    @Test
    void getRelatedProductsBackoffice_whenProductExists_thenReturnRelatedList() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        List<ProductListVm> result = productService.getRelatedProductsBackoffice(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void getRelatedProductsBackoffice_whenProductNotFound_thenThrowNotFoundException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getRelatedProductsBackoffice(999L));
    }

    // ========== getProductVariationsByParentId ==========

    @Test
    void getProductVariationsByParentId_whenProductHasNoOptions_thenReturnEmptyList() {
        product.setHasOptions(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        var result = productService.getProductVariationsByParentId(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void getProductVariationsByParentId_whenProductNotFound_thenThrowNotFoundException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
            () -> productService.getProductVariationsByParentId(999L));
    }

    // ========== deleteProduct with child product ==========

    @Test
    void deleteProduct_whenProductIsVariant_thenRemoveCombinationsAndUnpublish() {
        Product parent = new Product();
        parent.setId(10L);
        product.setParent(parent);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(product))
            .thenReturn(Collections.emptyList());

        productService.deleteProduct(1L);

        assertThat(product.isPublished()).isFalse();
        verify(productRepository).save(product);
    }

}
