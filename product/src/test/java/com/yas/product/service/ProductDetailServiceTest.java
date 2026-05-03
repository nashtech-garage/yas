 package com.yas.product.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDetailServiceTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    MediaService mediaService;
    @Mock
    ProductOptionCombinationRepository productOptionCombinationRepository;

    @InjectMocks
    ProductDetailService productDetailService;

    @Test
    void testGetProductDetailById_NotFound() {
        // Giả lập không tìm thấy sản phẩm hoặc sản phẩm chưa được publish
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(1L));
    }

    @Test
    void testGetProductDetailById_Success_WithFullData() {
        // 1. Setup Product cha
        Product product = new Product();
        product.setId(1L);
        product.setName("Parent Product");
        product.setPublished(true);     // Bắt buộc phải true để vượt qua bộ lọc isPublished()
        product.setHasOptions(true);    // Bắt buộc true để chạy vào nhánh lấy Variations
        product.setThumbnailMediaId(10L);

        // 2. Setup Brand & Category & Image
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Test Brand");
        product.setBrand(brand);

        ProductCategory pc = new ProductCategory();
        pc.setCategory(new Category());
        product.setProductCategories(List.of(pc));

        ProductImage pi = new ProductImage();
        pi.setImageId(20L);
        product.setProductImages(List.of(pi));

        // 3. Setup Attribute
        ProductAttribute attr = new ProductAttribute();
        attr.setId(1L);
        attr.setName("Color");
        
        ProductAttributeValue pav = new ProductAttributeValue();
        pav.setId(1L);
        pav.setProductAttribute(attr);
        pav.setValue("Red");
        product.setAttributeValues(List.of(pav));

        // 4. Setup Product Variation (Sản phẩm con)
        Product variation = new Product();
        variation.setId(2L);
        variation.setName("Child Product");
        variation.setPublished(true); // Bắt buộc true để vượt qua bộ lọc isPublished() của list con
        product.setProducts(List.of(variation));

        // 5. Setup Option Combination cho con
        ProductOption option = new ProductOption();
        option.setId(100L);
        ProductOptionCombination poc = new ProductOptionCombination();
        poc.setProductOption(option);
        poc.setValue("Red Option");

        // 6. Định nghĩa hành vi của Mocks
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(any())).thenReturn(List.of(poc));
        
        // Dùng lenient để né lỗi UnnecessaryStubbing phòng trường hợp không gọi tới
        NoFileMediaVm mockMedia = mock(NoFileMediaVm.class);
        org.mockito.Mockito.lenient().when(mockMedia.url()).thenReturn("http://test-url.com");
        org.mockito.Mockito.lenient().when(mediaService.getMedia(anyLong())).thenReturn(mockMedia);

        // 7. Thực thi hàm
        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        // 8. Assert kết quả (Kết hợp cả Lombok Getter và Record Getter)
        assertNotNull(result);
        assertEquals("Parent Product", result.getName());               // Dùng getName() của Lombok
        assertEquals("Test Brand", result.getBrandName());              // Dùng getBrandName() của Lombok
        assertEquals(1, result.getAttributeValues().size());            // Dùng getAttributeValues() của Lombok
        
        assertEquals(1, result.getVariations().size());                 // Dùng getVariations() của Lombok
        assertEquals("Child Product", result.getVariations().get(0).name()); // Dùng name() của Record
        assertEquals("Red Option", result.getVariations().get(0).options().get(100L)); // Dùng options() của Record
    }
}