package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.*;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import com.yas.product.viewmodel.NoFileMediaVm; // Đảm bảo import đúng class này
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
    private final Long PRODUCT_ID = 1L;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(PRODUCT_ID);
        product.setPublished(true);
        product.setHasOptions(false);
        product.setAttributeValues(Collections.emptyList()); 
        product.setProductCategories(Collections.emptyList());
        product.setProducts(Collections.emptyList());
    }

    @Test
    void getProductDetailById_SimpleProduct_Success() {
        // Arrange
        product.setName("Test Product");
        product.setThumbnailMediaId(10L);
        
        // SỬA LỖI: Mock đúng class NoFileMediaVm mà Service yêu cầu
        NoFileMediaVm mockMedia = mock(NoFileMediaVm.class);
        when(mockMedia.url()).thenReturn("http://example.com/image.jpg");
        
        when(mediaService.getMedia(10L)).thenReturn(mockMedia);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        // Act
        ProductDetailInfoVm result = productDetailService.getProductDetailById(PRODUCT_ID);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertNotNull(result.getThumbnail());
        assertEquals("http://example.com/image.jpg", result.getThumbnail().url());
    }

    @Test
    void getProductDetailById_WithVariations_Success() {
        // Arrange
        product.setHasOptions(true);
        
        Product variation = new Product();
        variation.setId(2L);
        variation.setPublished(true);
        variation.setHasOptions(false);
        variation.setAttributeValues(Collections.emptyList());
        variation.setProductCategories(Collections.emptyList());
        variation.setProducts(Collections.emptyList());
        
        product.setProducts(List.of(variation)); 

        ProductOption option = new ProductOption();
        option.setId(100L);
        
        ProductOptionCombination poc = mock(ProductOptionCombination.class);
        when(poc.getProductOption()).thenReturn(option);
        when(poc.getValue()).thenReturn("Red");
        
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of(poc));

        // Act
        ProductDetailInfoVm result = productDetailService.getProductDetailById(PRODUCT_ID);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getVariations());
        assertFalse(result.getVariations().isEmpty());
        assertEquals("Red", result.getVariations().get(0).options().get(100L));
    }

    @Test
    void getProductDetailById_NotFound_ThrowsException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(999L));
    }
}
