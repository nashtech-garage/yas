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
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ProductDetailServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MediaService mediaService;
    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @InjectMocks
    private ProductDetailService productDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductDetailById_whenProductFound_thenReturnDetail() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setThumbnailMediaId(10L);
        product.setPublished(true);
        
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Brand 1");
        product.setBrand(brand);

        Category category = new Category();
        category.setId(1L);
        ProductCategory pc = new ProductCategory();
        pc.setCategory(category);
        product.setProductCategories(List.of(pc));

        product.setHasOptions(true);
        Product variant = new Product();
        variant.setId(2L);
        variant.setName("Variant 1");
        variant.setPublished(true);
        product.setProducts(List.of(variant));

        ProductOption option = new ProductOption();
        option.setId(1L);
        ProductOptionCombination poc = new ProductOptionCombination();
        poc.setProductOption(option);
        poc.setValue("Value 1");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumb"));
        when(productOptionCombinationRepository.findAllByProduct(variant)).thenReturn(List.of(poc));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result);
        assertEquals("Product 1", result.getName());
        assertEquals(1, result.getVariations().size());
        assertEquals("Brand 1", result.getBrandName());
    }

    @Test
    void getProductDetailById_whenNotPublished_thenThrowException() {
        Product product = new Product();
        product.setId(1L);
        product.setPublished(false);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(1L));
    }
}
