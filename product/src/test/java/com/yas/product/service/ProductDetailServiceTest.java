package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
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
    private Brand brand;
    private Category category;

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(1L);
        brand.setName("TestBrand");

        category = new Category();
        category.setId(1L);
        category.setName("TestCategory");

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .shortDescription("Short desc")
                .description("Description")
                .specification("Spec")
                .sku("SKU001")
                .gtin("GTIN001")
                .slug("test-product")
                .isAllowedToOrder(true)
                .isPublished(true)
                .isFeatured(false)
                .isVisibleIndividually(true)
                .stockTrackingEnabled(true)
                .price(100.0)
                .metaTitle("Meta Title")
                .metaKeyword("Meta Keyword")
                .metaDescription("Meta Description")
                .taxClassId(1L)
                .brand(brand)
                .build();
    }

    @Test
    void getProductDetailById_whenProductExists_shouldReturnProductDetail() {
        ProductCategory productCategory = ProductCategory.builder()
                .product(product)
                .category(category)
                .build();
        product.setProductCategories(List.of(productCategory));
        product.setAttributeValues(new ArrayList<>());
        product.setHasOptions(false);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals("Short desc", result.getShortDescription());
        assertEquals("Description", result.getDescription());
        assertEquals("SKU001", result.getSku());
        assertEquals("test-product", result.getSlug());
        assertEquals(100.0, result.getPrice());
        assertEquals(1L, result.getBrandId());
        assertEquals("TestBrand", result.getBrandName());
        assertEquals(1, result.getCategories().size());
        assertTrue(result.getVariations().isEmpty());
    }

    @Test
    void getProductDetailById_whenProductNotFound_shouldThrowNotFoundException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(999L));
    }

    @Test
    void getProductDetailById_whenProductNotPublished_shouldThrowNotFoundException() {
        product.setPublished(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(1L));
    }

    @Test
    void getProductDetailById_whenProductHasNoBrand_shouldReturnNullBrand() {
        product.setBrand(null);
        product.setProductCategories(Collections.emptyList());
        product.setAttributeValues(new ArrayList<>());
        product.setHasOptions(false);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNull(result.getBrandId());
        assertNull(result.getBrandName());
    }

    @Test
    void getProductDetailById_whenProductHasNullCategories_shouldReturnEmptyCategories() {
        product.setProductCategories(null);
        product.setAttributeValues(new ArrayList<>());
        product.setHasOptions(false);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertTrue(result.getCategories().isEmpty());
    }

    @Test
    void getProductDetailById_whenProductHasThumbnail_shouldReturnThumbnailImage() {
        product.setThumbnailMediaId(10L);
        product.setProductCategories(Collections.emptyList());
        product.setAttributeValues(new ArrayList<>());
        product.setHasOptions(false);

        NoFileMediaVm mediaVm = new NoFileMediaVm(10L, "caption", "file.jpg", "image/jpeg",
                "http://example.com/file.jpg");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(10L)).thenReturn(mediaVm);

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result.getThumbnail());
        assertEquals(10L, result.getThumbnail().id());
        assertEquals("http://example.com/file.jpg", result.getThumbnail().url());
    }

    @Test
    void getProductDetailById_whenProductHasNoThumbnail_shouldReturnNullThumbnail() {
        product.setThumbnailMediaId(null);
        product.setProductCategories(Collections.emptyList());
        product.setAttributeValues(new ArrayList<>());
        product.setHasOptions(false);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNull(result.getThumbnail());
    }

    @Test
    void getProductDetailById_whenProductHasImages_shouldReturnProductImages() {
        ProductImage image1 = ProductImage.builder().imageId(20L).product(product).build();
        ProductImage image2 = ProductImage.builder().imageId(21L).product(product).build();
        product.setProductImages(List.of(image1, image2));
        product.setProductCategories(Collections.emptyList());
        product.setAttributeValues(new ArrayList<>());
        product.setHasOptions(false);

        NoFileMediaVm mediaVm20 = new NoFileMediaVm(20L, "cap", "f1.jpg", "image/jpeg", "http://example.com/f1.jpg");
        NoFileMediaVm mediaVm21 = new NoFileMediaVm(21L, "cap", "f2.jpg", "image/jpeg", "http://example.com/f2.jpg");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(20L)).thenReturn(mediaVm20);
        when(mediaService.getMedia(21L)).thenReturn(mediaVm21);

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertEquals(2, result.getProductImages().size());
        assertEquals("http://example.com/f1.jpg", result.getProductImages().get(0).url());
    }

    @Test
    void getProductDetailById_whenProductHasVariations_shouldReturnVariations() {
        product.setProductCategories(Collections.emptyList());
        product.setAttributeValues(new ArrayList<>());
        product.setHasOptions(true);

        Product variation = Product.builder()
                .id(2L)
                .name("Variation 1")
                .slug("variation-1")
                .sku("SKU-VAR-1")
                .gtin("GTIN-VAR-1")
                .price(120.0)
                .isPublished(true)
                .parent(product)
                .build();
        variation.setProductImages(new ArrayList<>());

        product.setProducts(List.of(variation));

        ProductOption option = new ProductOption();
        option.setId(1L);
        option.setName("Color");

        ProductOptionCombination combination = ProductOptionCombination.builder()
                .product(variation)
                .productOption(option)
                .value("Red")
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of(combination));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertEquals(1, result.getVariations().size());
        assertEquals("Variation 1", result.getVariations().get(0).name());
        assertEquals("Red", result.getVariations().get(0).options().get(1L));
    }

    @Test
    void getProductDetailById_whenVariationNotPublished_shouldFilterOut() {
        product.setProductCategories(Collections.emptyList());
        product.setAttributeValues(new ArrayList<>());
        product.setHasOptions(true);

        Product unpublishedVariation = Product.builder()
                .id(3L)
                .name("Unpublished Variation")
                .slug("unpublished-var")
                .sku("SKU-UNPU")
                .price(100.0)
                .isPublished(false)
                .parent(product)
                .build();

        product.setProducts(List.of(unpublishedVariation));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertTrue(result.getVariations().isEmpty());
    }
}
