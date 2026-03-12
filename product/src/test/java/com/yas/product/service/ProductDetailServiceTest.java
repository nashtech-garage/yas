package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.model.ProductImage;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Iphone");
        product.setPublished(true); // FIX: Dùng setPublished thay vì setIsPublished
        
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Apple");
        product.setBrand(brand);
        
        product.setThumbnailMediaId(10L);
        
        ProductImage image = new ProductImage();
        image.setImageId(11L);
        product.setProductImages(List.of(image));
    }

    @Test
    void getProductDetailById_WhenValid_ShouldReturnData() {
        // Mock Product
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        // FIX: Mock NoFileMediaVm thay vì ImageVm
        NoFileMediaVm thumbMedia = mock(NoFileMediaVm.class);
        when(thumbMedia.url()).thenReturn("thumb-url");
        when(mediaService.getMedia(10L)).thenReturn(thumbMedia);

        NoFileMediaVm imgMedia = mock(NoFileMediaVm.class);
        when(imgMedia.url()).thenReturn("img-url");
        when(mediaService.getMedia(11L)).thenReturn(imgMedia);

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);
        
        assertNotNull(result);
        // FIX: Dùng getName() và getBrandName()
        assertEquals("Iphone", result.getName());
        assertEquals("Apple", result.getBrandName());
    }

    @Test
    void getProductDetailById_WhenProductNotFound_ShouldThrowNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(1L));
    }

    @Test
    void getProductDetailById_WhenProductNotPublished_ShouldThrowNotFoundException() {
        product.setPublished(false); // FIX: Dùng setPublished
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(1L));
    }
}