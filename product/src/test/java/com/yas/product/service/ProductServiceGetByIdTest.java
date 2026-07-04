package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
class ProductServiceGetByIdTest {

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

    @Test
    void getProductById_whenProductNotFound_thenThrowNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void getProductById_whenProductHasImagesAndCategoriesAndBrand_thenReturnProductDetailVmWithAllFields() {
        Product p = new Product();
        p.setId(1L);
        p.setName("Product 1");
        p.setThumbnailMediaId(1L);
        
        com.yas.product.model.ProductImage pi = new com.yas.product.model.ProductImage();
        pi.setImageId(2L);
        p.setProductImages(List.of(pi));
        
        com.yas.product.model.ProductCategory pc = new com.yas.product.model.ProductCategory();
        com.yas.product.model.Category c = new com.yas.product.model.Category();
        c.setId(3L);
        pc.setCategory(c);
        p.setProductCategories(List.of(pc));
        
        Brand b = new Brand();
        b.setId(4L);
        p.setBrand(b);

        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "thumb-url"));
        when(mediaService.getMedia(2L)).thenReturn(new NoFileMediaVm(2L, "", "", "", "image-url"));

        com.yas.product.viewmodel.product.ProductDetailVm result = productService.getProductById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.brandId()).isEqualTo(4L);
        assertThat(result.thumbnailMedia().url()).isEqualTo("thumb-url");
        assertThat(result.productImageMedias().get(0).url()).isEqualTo("image-url");
    }
}
