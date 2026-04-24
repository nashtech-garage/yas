package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import java.util.Collections;
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
        sampleProduct.setProductCategories(List.of(productCategory));
        sampleProduct.setThumbnailMediaId(100L);
        sampleProduct.setIsPublished(true);
        sampleProduct.setIsVisibleIndividually(true);
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
        void getProductDetailById_whenProductExists_thenReturnProductDetailInfoVm() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productOptionCombinationRepository.findAllByProductParent(sampleProduct))
                .thenReturn(Collections.emptyList());
            when(mediaService.getMediaUrl(100L)).thenReturn("http://media/100");

            var result = productDetailService.getProductDetailById(1L);

            assertNotNull(result);
            assertEquals("Test Product", result.name());
            assertEquals("test-product", result.slug());
        }

        @Test
        void getProductDetailById_whenProductHasNoCategories_thenReturnEmptyCategories() {
            sampleProduct.setProductCategories(Collections.emptyList());

            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productOptionCombinationRepository.findAllByProductParent(sampleProduct))
                .thenReturn(Collections.emptyList());
            when(mediaService.getMediaUrl(100L)).thenReturn("http://media/100");

            var result = productDetailService.getProductDetailById(1L);

            assertNotNull(result);
            assertEquals(0, result.categories().size());
        }
    }
}
