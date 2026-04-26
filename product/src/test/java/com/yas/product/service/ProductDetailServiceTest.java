package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

    private Product sampleProduct;
    private static final NoFileMediaVm SAMPLE_MEDIA =
        new NoFileMediaVm(100L, "caption", "image.png", "image/png", "http://media/100");

    @BeforeEach
    void setUp() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("TestBrand");

        Category category = new Category();
        category.setId(1L);
        category.setName("TestCategory");

        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(category);

        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Test Product");
        sampleProduct.setSlug("test-product");
        sampleProduct.setBrand(brand);
        sampleProduct.setProductCategories(new ArrayList<>(List.of(productCategory)));
        sampleProduct.setThumbnailMediaId(100L);
        sampleProduct.setPublished(true);
        sampleProduct.setVisibleIndividually(true);
        sampleProduct.setHasOptions(false);
        sampleProduct.setProductImages(new ArrayList<>());
        sampleProduct.setAttributeValues(new ArrayList<>());
        sampleProduct.setProducts(new ArrayList<>());
    }

    @Nested
    class GetProductDetailByIdTest {

        @Test
        void getProductDetailById_whenProductNotFound_thenThrowNotFoundException() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            NotFoundException ex = assertThrows(NotFoundException.class,
                () -> productDetailService.getProductDetailById(999L));

            assertNotNull(ex.getMessage());
        }

        @Test
        void getProductDetailById_whenProductNotPublished_thenThrowNotFoundException() {
            sampleProduct.setPublished(false);
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

            assertThrows(NotFoundException.class,
                () -> productDetailService.getProductDetailById(1L));
        }

        @Test
        void getProductDetailById_whenProductExists_thenReturnProductDetailInfoVm() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(mediaService.getMedia(100L)).thenReturn(SAMPLE_MEDIA);

            var result = productDetailService.getProductDetailById(1L);

            assertNotNull(result);
            assertEquals("Test Product", result.getName());
            assertEquals("test-product", result.getSlug());
            assertEquals(1, result.getCategories().size());
            assertEquals("TestBrand", result.getBrandName());
            assertNotNull(result.getThumbnail());
        }

        @Test
        void getProductDetailById_whenProductHasNoCategories_thenReturnEmptyCategories() {
            sampleProduct.setProductCategories(new ArrayList<>());
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(mediaService.getMedia(100L)).thenReturn(SAMPLE_MEDIA);

            var result = productDetailService.getProductDetailById(1L);

            assertNotNull(result);
            assertEquals(0, result.getCategories().size());
        }

        @Test
        void getProductDetailById_whenProductHasNoBrand_thenBrandIsNull() {
            sampleProduct.setBrand(null);
            sampleProduct.setThumbnailMediaId(null);
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

            var result = productDetailService.getProductDetailById(1L);

            assertNotNull(result);
            assertNull(result.getBrandId());
            assertNull(result.getBrandName());
            assertNull(result.getThumbnail());
        }
    }
}
