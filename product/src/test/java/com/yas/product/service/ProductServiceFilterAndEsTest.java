package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
class ProductServiceFilterAndEsTest {

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

    // ========== getProductsWithFilter ==========

    @Test
    void getProductsWithFilter_whenProductsExist_thenReturnPaginatedResult() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.getProductsWithFilter(any(), any(), any(Pageable.class))).thenReturn(page);

        ProductListGetVm result = productService.getProductsWithFilter(0, 10, "Test", "");

        assertThat(result.productContent()).hasSize(1);
        assertThat(result.pageNo()).isEqualTo(0);
    }

    @Test
    void getProductsWithFilter_whenNoProductsFound_thenReturnEmptyPage() {
        Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());
        when(productRepository.getProductsWithFilter(any(), any(), any(Pageable.class))).thenReturn(emptyPage);

        ProductListGetVm result = productService.getProductsWithFilter(0, 10, "", "");

        assertThat(result.productContent()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0);
    }

    // ========== getProductEsDetailById ==========

    @Test
    void getProductEsDetailById_whenProductExists_thenReturnEsDetailVm() {
        product.setBrand(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductEsDetailVm result = productService.getProductEsDetailById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Test Product");
        assertThat(result.brand()).isNull();
    }

    @Test
    void getProductEsDetailById_whenProductHasBrand_thenReturnBrandName() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Brand A");
        product.setBrand(brand);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductEsDetailVm result = productService.getProductEsDetailById(1L);

        assertThat(result.brand()).isEqualTo("Brand A");
    }

    @Test
    void getProductEsDetailById_whenProductNotFound_thenThrowNotFoundException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductEsDetailById(999L));
    }

}
