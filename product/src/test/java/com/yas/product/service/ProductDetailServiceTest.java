/**
 * Unit tests for {@link ProductDetailService}.
 * Coverage target: >= 70% line coverage.
 * ProductDetailInfoVm is a Lombok @Getter/@Setter class, NOT a record.
 * Accessors are getId(), getName(), etc.
 */
package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductDetailServiceTest {

    private static final Long PRODUCT_ID = 1L;
    private static final Long BRAND_ID = 10L;
    private static final Long CATEGORY_ID = 20L;
    private static final Long THUMBNAIL_MEDIA_ID = 100L;
    private static final Long IMAGE_ID = 200L;
    private static final String PRODUCT_NAME = "Test Product";
    private static final String SLUG = "test-product";
    private static final String SKU = "SKU-001";
    private static final String BRAND_NAME = "Test Brand";
    private static final String THUMBNAIL_URL = "http://media/thumb.jpg";
    private static final String IMAGE_URL = "http://media/image.jpg";

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MediaService mediaService;
    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @InjectMocks
    private ProductDetailService productDetailService;

    private Product buildProduct() {
        Brand brand = new Brand();
        brand.setId(BRAND_ID);
        brand.setName(BRAND_NAME);

        Category category = new Category();
        category.setId(CATEGORY_ID);
        category.setName("Test Category");

        Product product = Product.builder()
                .id(PRODUCT_ID)
                .name(PRODUCT_NAME)
                .slug(SLUG)
                .sku(SKU)
                .price(99.99)
                .isPublished(true)
                .isFeatured(false)
                .isAllowedToOrder(true)
                .isVisibleIndividually(true)
                .hasOptions(false)
                .thumbnailMediaId(THUMBNAIL_MEDIA_ID)
                .brand(brand)
                .build();

        ProductCategory pc = ProductCategory.builder().product(product).category(category).build();
        product.setProductCategories(List.of(pc));

        ProductImage img = ProductImage.builder().imageId(IMAGE_ID).product(product).build();
        product.setProductImages(List.of(img));

        product.setAttributeValues(new ArrayList<>());
        product.setProducts(new ArrayList<>());

        return product;
    }

    @Nested
    class GetProductDetailByIdTest {

        @Test
        void testGetProductDetailById_whenProductExists_shouldReturnDetailVm() {
            Product product = buildProduct();
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(mediaService.getMedia(THUMBNAIL_MEDIA_ID))
                    .thenReturn(new NoFileMediaVm(THUMBNAIL_MEDIA_ID, "", "", "", THUMBNAIL_URL));
            when(mediaService.getMedia(IMAGE_ID))
                    .thenReturn(new NoFileMediaVm(IMAGE_ID, "", "", "", IMAGE_URL));

            ProductDetailInfoVm result = productDetailService.getProductDetailById(PRODUCT_ID);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(PRODUCT_ID);
            assertThat(result.getName()).isEqualTo(PRODUCT_NAME);
            assertThat(result.getSlug()).isEqualTo(SLUG);
            assertThat(result.getBrandId()).isEqualTo(BRAND_ID);
            assertThat(result.getBrandName()).isEqualTo(BRAND_NAME);
            assertThat(result.getThumbnail()).isNotNull();
            assertThat(result.getProductImages()).hasSize(1);
        }

        @Test
        void testGetProductDetailById_whenProductNotFound_shouldThrowNotFoundException() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> productDetailService.getProductDetailById(PRODUCT_ID));
        }

        @Test
        void testGetProductDetailById_whenProductNotPublished_shouldThrowNotFoundException() {
            Product product = buildProduct();
            product.setPublished(false);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

            assertThrows(NotFoundException.class,
                    () -> productDetailService.getProductDetailById(PRODUCT_ID));
        }

        @Test
        void testGetProductDetailById_whenNoBrand_shouldReturnNullBrandFields() {
            Product product = buildProduct();
            product.setBrand(null);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(mediaService.getMedia(anyLong()))
                    .thenReturn(new NoFileMediaVm(1L, "", "", "", THUMBNAIL_URL));

            ProductDetailInfoVm result = productDetailService.getProductDetailById(PRODUCT_ID);

            assertThat(result.getBrandId()).isNull();
            assertThat(result.getBrandName()).isNull();
        }

        @Test
        void testGetProductDetailById_whenNoCategories_shouldReturnEmptyCategories() {
            Product product = buildProduct();
            product.setProductCategories(null);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(mediaService.getMedia(anyLong()))
                    .thenReturn(new NoFileMediaVm(1L, "", "", "", THUMBNAIL_URL));

            ProductDetailInfoVm result = productDetailService.getProductDetailById(PRODUCT_ID);

            assertThat(result.getCategories()).isEmpty();
        }

        @Test
        void testGetProductDetailById_whenNoThumbnail_shouldReturnNullThumbnail() {
            Product product = buildProduct();
            product.setThumbnailMediaId(null);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(mediaService.getMedia(IMAGE_ID))
                    .thenReturn(new NoFileMediaVm(IMAGE_ID, "", "", "", IMAGE_URL));

            ProductDetailInfoVm result = productDetailService.getProductDetailById(PRODUCT_ID);

            assertThat(result.getThumbnail()).isNull();
        }

        @Test
        void testGetProductDetailById_whenNoImages_shouldReturnEmptyImages() {
            Product product = buildProduct();
            product.setProductImages(null);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(mediaService.getMedia(THUMBNAIL_MEDIA_ID))
                    .thenReturn(new NoFileMediaVm(THUMBNAIL_MEDIA_ID, "", "", "", THUMBNAIL_URL));

            ProductDetailInfoVm result = productDetailService.getProductDetailById(PRODUCT_ID);

            assertThat(result.getProductImages()).isEmpty();
        }

        @Test
        void testGetProductDetailById_whenHasOptions_shouldReturnVariations() {
            Product product = buildProduct();
            product.setHasOptions(true);

            Product variation = Product.builder()
                    .id(2L).name("Var 1").slug("var-1").sku("V-SKU").price(50.0)
                    .isPublished(true).parent(product)
                    .thumbnailMediaId(THUMBNAIL_MEDIA_ID)
                    .build();
            variation.setProductImages(new ArrayList<>());
            product.setProducts(List.of(variation));

            ProductOption option = new ProductOption();
            option.setId(5L);
            option.setName("Color");

            ProductOptionCombination combo = ProductOptionCombination.builder()
                    .product(variation).productOption(option).value("Red").build();

            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(mediaService.getMedia(anyLong()))
                    .thenReturn(new NoFileMediaVm(1L, "", "", "", THUMBNAIL_URL));
            when(productOptionCombinationRepository.findAllByProduct(variation))
                    .thenReturn(List.of(combo));

            ProductDetailInfoVm result = productDetailService.getProductDetailById(PRODUCT_ID);

            assertThat(result.getVariations()).hasSize(1);
            assertThat(result.getVariations().get(0).name()).isEqualTo("Var 1");
        }

        @Test
        void testGetProductDetailById_whenHasOptionsButVariationNotPublished_shouldExclude() {
            Product product = buildProduct();
            product.setHasOptions(true);

            Product variation = Product.builder()
                    .id(2L).name("Var 1").slug("var-1").sku("V-SKU").price(50.0)
                    .isPublished(false).parent(product).build();
            variation.setProductImages(new ArrayList<>());
            product.setProducts(List.of(variation));

            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(mediaService.getMedia(anyLong()))
                    .thenReturn(new NoFileMediaVm(1L, "", "", "", THUMBNAIL_URL));

            ProductDetailInfoVm result = productDetailService.getProductDetailById(PRODUCT_ID);

            assertThat(result.getVariations()).isEmpty();
        }

        @Test
        void testGetProductDetailById_whenHasAttributes_shouldReturnAttributes() {
            Product product = buildProduct();

            ProductAttribute attr = ProductAttribute.builder().id(1L).name("Material").build();
            ProductAttributeValue attrVal = new ProductAttributeValue();
            attrVal.setId(1L);
            attrVal.setProductAttribute(attr);
            attrVal.setValue("Cotton");
            attrVal.setProduct(product);
            product.setAttributeValues(List.of(attrVal));

            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(mediaService.getMedia(anyLong()))
                    .thenReturn(new NoFileMediaVm(1L, "", "", "", THUMBNAIL_URL));

            ProductDetailInfoVm result = productDetailService.getProductDetailById(PRODUCT_ID);

            assertThat(result.getAttributeValues()).hasSize(1);
        }
    }
}
