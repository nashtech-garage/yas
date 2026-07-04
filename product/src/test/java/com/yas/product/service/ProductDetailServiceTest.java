package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
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
class ProductDetailServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MediaService mediaService;

    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @InjectMocks
    private ProductDetailService productDetailService;

    private Product product;
    private static final NoFileMediaVm EMPTY_MEDIA = new NoFileMediaVm(null, "", "", "", "");

    @BeforeEach
    void setUp() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Test Brand");

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(category);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setSlug("test-product");
        product.setSku("SKU-001");
        product.setGtin("GTIN-001");
        product.setPublished(true);
        product.setHasOptions(false);
        product.setBrand(brand);
        product.setProductCategories(List.of(productCategory));
        product.setAttributeValues(Collections.emptyList());
        product.setProductImages(Collections.emptyList());
        product.setProducts(Collections.emptyList());
    }

    @Test
    void getProductDetailById_whenProductExists_thenReturnDetailVm() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getSlug()).isEqualTo("test-product");
        assertThat(result.getBrandId()).isEqualTo(1L);
        assertThat(result.getBrandName()).isEqualTo("Test Brand");
        assertThat(result.getCategories()).hasSize(1);
    }

    @Test
    void getProductDetailById_whenProductNotFound_thenThrowNotFoundException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
            () -> productDetailService.getProductDetailById(999L));
    }

    @Test
    void getProductDetailById_whenProductNotPublished_thenThrowNotFoundException() {
        product.setPublished(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(NotFoundException.class,
            () -> productDetailService.getProductDetailById(1L));
    }

    @Test
    void getProductDetailById_whenProductHasNoBrand_thenReturnNullBrandId() {
        product.setBrand(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertThat(result.getBrandId()).isNull();
        assertThat(result.getBrandName()).isNull();
    }

    @Test
    void getProductDetailById_whenProductHasNoCategories_thenReturnEmptyList() {
        product.setProductCategories(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertThat(result.getCategories()).isEmpty();
    }

    @Test
    void getProductDetailById_whenProductHasOptions_thenReturnVariations() {
        product.setHasOptions(true);

        Product variation = new Product();
        variation.setId(2L);
        variation.setName("Variation 1");
        variation.setSlug("variation-1");
        variation.setPublished(true);
        variation.setProductImages(Collections.emptyList());

        product.setProducts(List.of(variation));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(variation))
            .thenReturn(Collections.emptyList());

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertThat(result.getVariations()).hasSize(1);
        assertThat(result.getVariations().get(0).id()).isEqualTo(2L);
    }

    @Test
    void getProductDetailById_whenVariationNotPublished_thenExcludeFromResult() {
        product.setHasOptions(true);

        Product unpublishedVariation = new Product();
        unpublishedVariation.setId(2L);
        unpublishedVariation.setPublished(false);

        product.setProducts(List.of(unpublishedVariation));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertThat(result.getVariations()).isEmpty();
    }

    @Test
    void getProductDetailById_whenProductHasThumbnail_thenReturnThumbnailImageVm() {
        product.setThumbnailMediaId(10L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(10L))
            .thenReturn(new NoFileMediaVm(10L, "thumb", "thumb.png", "image/png", "http://example.com/thumb.png"));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertThat(result.getThumbnail()).isNotNull();
        assertThat(result.getThumbnail().id()).isEqualTo(10L);
        assertThat(result.getThumbnail().url()).isEqualTo("http://example.com/thumb.png");
    }
}
