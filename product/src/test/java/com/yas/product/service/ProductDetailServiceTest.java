package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import java.util.ArrayList;
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

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L).name("Product").slug("product").sku("SKU").price(100.0)
                .isPublished(true).isVisibleIndividually(true).isAllowedToOrder(true)
                .isFeatured(false).thumbnailMediaId(1L)
                .productCategories(new ArrayList<>())
                .productImages(new ArrayList<>())
                .attributeValues(new ArrayList<>())
                .build();
    }

    @Test
    void getProductDetailById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "thumb-url"));
        var result = productDetailService.getProductDetailById(1L);
        assertNotNull(result);
        assertEquals("Product", result.getName());
        assertEquals("thumb-url", result.getThumbnail().url());
    }

    @Test
    void getProductDetailById_WithBrand() {
        Brand brand = new Brand();
        brand.setId(5L);
        brand.setName("BrandName");
        product.setBrand(brand);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));
        var result = productDetailService.getProductDetailById(1L);
        assertEquals(5L, result.getBrandId());
        assertEquals("BrandName", result.getBrandName());
    }

    @Test
    void getProductDetailById_WithCategories() {
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("Cat");
        ProductCategory pc = new ProductCategory();
        pc.setCategory(cat);
        product.setProductCategories(List.of(pc));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));
        var result = productDetailService.getProductDetailById(1L);
        assertEquals(1, result.getCategories().size());
        assertEquals("Cat", result.getCategories().get(0).getName());
    }

    @Test
    void getProductDetailById_WithVariations() {
        Product variation = Product.builder().id(2L).name("Var").slug("var").sku("V1")
                .price(50.0).isPublished(true).thumbnailMediaId(2L)
                .productImages(new ArrayList<>()).build();
        product.setHasOptions(true);
        product.setProducts(List.of(variation));
        ProductOption po = new ProductOption();
        po.setId(10L);
        ProductOptionCombination combo = ProductOptionCombination.builder()
                .product(variation).productOption(po).value("Red").build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of(combo));
        when(mediaService.getMedia(anyLong())).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));
        var result = productDetailService.getProductDetailById(1L);
        assertEquals(1, result.getVariations().size());
        assertEquals("Var", result.getVariations().get(0).name());
    }

    @Test
    void getProductDetailById_WithImages() {
        ProductImage img = ProductImage.builder().imageId(5L).product(product).build();
        product.setProductImages(List.of(img));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(anyLong())).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));
        var result = productDetailService.getProductDetailById(1L);
        assertEquals(1, result.getProductImages().size());
    }

    @Test
    void getProductDetailById_NoThumbnail() {
        product.setThumbnailMediaId(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var result = productDetailService.getProductDetailById(1L);
        assertNull(result.getThumbnail());
    }

    @Test
    void getProductDetailById_NotPublished() {
        product.setPublished(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(1L));
    }

    @Test
    void getProductDetailById_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(1L));
    }

    @Test
    void getProductDetailById_NullCategories() {
        product.setProductCategories(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));
        var result = productDetailService.getProductDetailById(1L);
        assertNotNull(result);
        assertTrue(result.getCategories().isEmpty());
    }

    @Test
    void getProductDetailById_NoBrand() {
        product.setBrand(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));
        var result = productDetailService.getProductDetailById(1L);
        assertNull(result.getBrandId());
        assertNull(result.getBrandName());
    }
}
