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
class ProductServiceAdvancedQueryTest {

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

    // ========== getProductDetail ==========

    @Test
    void getProductDetail_whenProductNotFound_thenThrowNotFoundException() {
        when(productRepository.findBySlugAndIsPublishedTrue("nonexistent-slug")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductDetail("nonexistent-slug"));
    }

    @Test
    void getProductDetail_whenProductExists_thenReturnProductDetailGetVm() {
        product.setThumbnailMediaId(10L);
        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.of(product));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumb.url"));

        ProductDetailGetVm result = productService.getProductDetail("test-product");

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Test Product");
        assertThat(result.thumbnailMediaUrl()).isEqualTo("http://thumb.url");
    }

    // ========== getProductsByMultiQuery ==========

    @Test
    void getProductsByMultiQuery_whenProductsExist_thenReturnProductsGetVm() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(any(), any(), any(), any(), any()))
            .thenReturn(page);
        
        product.setThumbnailMediaId(10L);
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumb.url"));

        ProductsGetVm result = productService.getProductsByMultiQuery(0, 10, "Test", "category", 0.0, 100.0);

        assertThat(result.productContent()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    // ========== getProductsForWarehouse ==========

    @Test
    void getProductsForWarehouse_whenCalled_thenReturnList() {
        when(productRepository.findProductForWarehouse(any(), any(), any(), any()))
            .thenReturn(List.of(product));

        List<ProductInfoVm> result = productService.getProductsForWarehouse("Test", "SKU", List.of(1L), FilterExistInWhSelection.ALL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Test Product");
    }

    // ========== getListFeaturedProducts ==========

    @Test
    void getListFeaturedProducts_whenCalled_thenReturnProductFeatureGetVm() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.getFeaturedProduct(any())).thenReturn(page);

        product.setThumbnailMediaId(10L);
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumb.url"));

        ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 10);

        assertThat(result.productList()).hasSize(1);
        assertThat(result.totalPage()).isEqualTo(1);
    }

}
