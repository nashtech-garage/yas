package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.Optional;
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

    @Test
    void getProductDetailById_whenProductNotFound_thenThrowNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(1L));
    }

    @Test
    void getProductDetailById_whenProductUnpublished_thenThrowNotFoundException() {
        Product unpublishedProduct = new Product();
        unpublishedProduct.setId(2L);
        unpublishedProduct.setPublished(false);
        when(productRepository.findById(2L)).thenReturn(Optional.of(unpublishedProduct));

        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(2L));
    }

    @Test
    void getProductDetailById_whenHasNoOptions_thenReturnDetailWithoutVariations() {
        Product product = buildProduct(10L, "Main product", true, false);
        product.setThumbnailMediaId(100L);

        Brand brand = new Brand();
        brand.setId(20L);
        brand.setName("YAS Brand");
        product.setBrand(brand);

        Category category = new Category();
        category.setId(30L);
        category.setName("Shoes");
        ProductCategory productCategory = ProductCategory.builder()
            .product(product)
            .category(category)
            .build();
        product.setProductCategories(List.of(productCategory));

        ProductImage image1 = ProductImage.builder().imageId(101L).product(product).build();
        ProductImage image2 = ProductImage.builder().imageId(102L).product(product).build();
        product.setProductImages(List.of(image1, image2));

        ProductAttribute productAttribute = ProductAttribute.builder().id(200L).name("Color").build();
        ProductAttributeValue productAttributeValue = new ProductAttributeValue();
        productAttributeValue.setId(201L);
        productAttributeValue.setProduct(product);
        productAttributeValue.setProductAttribute(productAttribute);
        productAttributeValue.setValue("Black");
        product.setAttributeValues(List.of(productAttributeValue));

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(100L)).thenReturn(new NoFileMediaVm(100L, "", "", "", "thumb-url"));
        when(mediaService.getMedia(101L)).thenReturn(new NoFileMediaVm(101L, "", "", "", "image-1-url"));
        when(mediaService.getMedia(102L)).thenReturn(new NoFileMediaVm(102L, "", "", "", "image-2-url"));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Main product", result.getName());
        assertEquals(20L, result.getBrandId());
        assertEquals("YAS Brand", result.getBrandName());
        assertEquals(1, result.getCategories().size());
        assertEquals("Shoes", result.getCategories().getFirst().getName());
        assertEquals(1, result.getAttributeValues().size());
        assertEquals("Color", result.getAttributeValues().getFirst().nameProductAttribute());
        assertEquals("thumb-url", result.getThumbnail().url());
        assertEquals(2, result.getProductImages().size());
        assertTrue(result.getVariations().isEmpty());
        verify(productOptionCombinationRepository, never()).findAllByProduct(product);
    }

    @Test
    void getProductDetailById_whenHasOptions_thenReturnOnlyPublishedVariations() {
        Product mainProduct = buildProduct(11L, "Main product", true, true);

        Product variation = buildProduct(12L, "Variation Red", true, false);
        variation.setParent(mainProduct);
        variation.setThumbnailMediaId(300L);
        variation.setProductImages(List.of(ProductImage.builder().imageId(301L).product(variation).build()));

        Product hiddenVariation = buildProduct(13L, "Hidden variation", false, false);
        hiddenVariation.setParent(mainProduct);

        mainProduct.setProducts(List.of(variation, hiddenVariation));

        ProductOption option = new ProductOption();
        option.setId(400L);
        option.setName("Color");
        ProductOptionCombination combination = ProductOptionCombination.builder()
            .product(variation)
            .productOption(option)
            .value("Red")
            .displayOrder(1)
            .build();

        when(productRepository.findById(11L)).thenReturn(Optional.of(mainProduct));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of(combination));
        when(mediaService.getMedia(300L)).thenReturn(new NoFileMediaVm(300L, "", "", "", "var-thumb-url"));
        when(mediaService.getMedia(301L)).thenReturn(new NoFileMediaVm(301L, "", "", "", "var-image-url"));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(11L);

        assertNotNull(result);
        assertEquals(1, result.getVariations().size());
        var variationVm = result.getVariations().getFirst();
        assertEquals(12L, variationVm.id());
        assertEquals("var-thumb-url", variationVm.thumbnail().url());
        assertEquals("Red", variationVm.options().get(400L));
        assertEquals(1, variationVm.productImages().size());
        verify(productOptionCombinationRepository).findAllByProduct(variation);
        verify(productOptionCombinationRepository, never()).findAllByProduct(hiddenVariation);
    }

    private Product buildProduct(Long id, String name, boolean published, boolean hasOptions) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setSlug("slug-" + id);
        product.setSku("sku-" + id);
        product.setGtin("gtin-" + id);
        product.setPrice(100.0);
        product.setPublished(published);
        product.setHasOptions(hasOptions);
        product.setAllowedToOrder(true);
        product.setFeatured(false);
        product.setVisibleIndividually(true);
        product.setStockTrackingEnabled(true);
        product.setProductCategories(List.of());
        product.setAttributeValues(List.of());
        product.setProductImages(List.of());
        product.setProducts(List.of());
        return product;
    }
}
