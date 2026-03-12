package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductRelated;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import java.util.ArrayList;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MediaService mediaService;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductCategoryRepository productCategoryRepository;
    @Mock
    private ProductImageRepository productImageRepository;
    @Mock
    private ProductOptionRepository productOptionRepository;
    @Mock
    private ProductOptionValueRepository productOptionValueRepository;
    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;
    @Mock
    private ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Brand brand;
    private Category category;

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(1L);
        brand.setName("TestBrand");
        brand.setSlug("test-brand");

        category = new Category();
        category.setId(1L);
        category.setName("TestCategory");
        category.setSlug("test-category");

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
                .isFeatured(true)
                .isVisibleIndividually(true)
                .stockTrackingEnabled(false)
                .price(99.99)
                .metaTitle("Meta Title")
                .metaKeyword("keyword")
                .metaDescription("Meta Desc")
                .taxClassId(1L)
                .brand(brand)
                .thumbnailMediaId(10L)
                .build();
    }

    @Nested
    class GetProductsWithFilterTest {

        @Test
        void getProductsWithFilter_shouldReturnPaginatedProducts() {
            Product product2 = Product.builder()
                    .id(2L).name("Product 2").slug("product-2")
                    .isPublished(true).price(50.0).build();
            List<Product> products = List.of(product, product2);
            Page<Product> productPage = new PageImpl<>(products);

            when(productRepository.getProductsWithFilter(any(), any(), any(Pageable.class)))
                    .thenReturn(productPage);

            ProductListGetVm result = productService.getProductsWithFilter(0, 10, "test", "");

            assertNotNull(result);
            assertEquals(2, result.productContent().size());
            assertEquals(0, result.pageNo());
            assertEquals(2, result.pageSize());
        }

        @Test
        void getProductsWithFilter_whenNoResults_shouldReturnEmptyList() {
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());

            when(productRepository.getProductsWithFilter(any(), any(), any(Pageable.class)))
                    .thenReturn(emptyPage);

            ProductListGetVm result = productService.getProductsWithFilter(0, 10, "nonexistent", "");

            assertTrue(result.productContent().isEmpty());
            assertEquals(0, result.totalElements());
        }
    }

    @Nested
    class GetProductByIdTest {

        @Test
        void getProductById_whenProductExists_shouldReturnProductDetail() {
            product.setProductCategories(new ArrayList<>());
            product.setProductImages(null);

            NoFileMediaVm mediaVm = new NoFileMediaVm(10L, "cap", "thumb.jpg", "image/jpeg",
                    "http://example.com/thumb.jpg");
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(mediaService.getMedia(10L)).thenReturn(mediaVm);

            ProductDetailVm result = productService.getProductById(1L);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("Test Product", result.name());
            assertEquals(99.99, result.price());
            assertEquals(1L, result.brandId());
            assertNotNull(result.thumbnailMedia());
        }

        @Test
        void getProductById_whenProductNotFound_shouldThrowNotFoundException() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.getProductById(999L));
        }

        @Test
        void getProductById_whenProductHasNoBrand_shouldReturnNullBrandId() {
            product.setBrand(null);
            product.setProductCategories(new ArrayList<>());
            product.setProductImages(null);
            product.setThumbnailMediaId(null);

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductDetailVm result = productService.getProductById(1L);

            assertNull(result.brandId());
        }

        @Test
        void getProductById_whenProductHasCategories_shouldReturnCategories() {
            ProductCategory pc = ProductCategory.builder()
                    .product(product).category(category).build();
            product.setProductCategories(List.of(pc));
            product.setProductImages(null);

            NoFileMediaVm mediaVm = new NoFileMediaVm(10L, "cap", "thumb.jpg", "image/jpeg",
                    "http://example.com/thumb.jpg");
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(mediaService.getMedia(10L)).thenReturn(mediaVm);

            ProductDetailVm result = productService.getProductById(1L);

            assertEquals(1, result.categories().size());
            assertEquals("TestCategory", result.categories().get(0).getName());
        }

        @Test
        void getProductById_whenProductHasImages_shouldReturnImages() {
            product.setProductCategories(new ArrayList<>());
            ProductImage image = ProductImage.builder().imageId(20L).product(product).build();
            product.setProductImages(List.of(image));

            NoFileMediaVm thumbMedia = new NoFileMediaVm(10L, "cap", "thumb.jpg", "image/jpeg",
                    "http://example.com/thumb.jpg");
            NoFileMediaVm imageMedia = new NoFileMediaVm(20L, "cap", "img.jpg", "image/jpeg",
                    "http://example.com/img.jpg");
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(mediaService.getMedia(10L)).thenReturn(thumbMedia);
            when(mediaService.getMedia(20L)).thenReturn(imageMedia);

            ProductDetailVm result = productService.getProductById(1L);

            assertEquals(1, result.productImageMedias().size());
            assertEquals(20L, result.productImageMedias().get(0).id());
        }
    }

    @Nested
    class GetLatestProductsTest {

        @Test
        void getLatestProducts_whenCountIsPositive_shouldReturnProducts() {
            when(productRepository.getLatestProducts(any(Pageable.class)))
                    .thenReturn(List.of(product));

            List<ProductListVm> result = productService.getLatestProducts(5);

            assertEquals(1, result.size());
            assertEquals("Test Product", result.get(0).name());
        }

        @Test
        void getLatestProducts_whenCountIsZero_shouldReturnEmptyList() {
            List<ProductListVm> result = productService.getLatestProducts(0);

            assertTrue(result.isEmpty());
        }

        @Test
        void getLatestProducts_whenCountIsNegative_shouldReturnEmptyList() {
            List<ProductListVm> result = productService.getLatestProducts(-1);

            assertTrue(result.isEmpty());
        }

        @Test
        void getLatestProducts_whenNoProducts_shouldReturnEmptyList() {
            when(productRepository.getLatestProducts(any(Pageable.class)))
                    .thenReturn(Collections.emptyList());

            List<ProductListVm> result = productService.getLatestProducts(5);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class GetProductsByBrandTest {

        @Test
        void getProductsByBrand_whenBrandExists_shouldReturnProducts() {
            when(brandRepository.findBySlug("test-brand")).thenReturn(Optional.of(brand));
            when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand))
                    .thenReturn(List.of(product));

            NoFileMediaVm mediaVm = new NoFileMediaVm(10L, "cap", "thumb.jpg", "image/jpeg",
                    "http://example.com/thumb.jpg");
            when(mediaService.getMedia(10L)).thenReturn(mediaVm);

            List<ProductThumbnailVm> result = productService.getProductsByBrand("test-brand");

            assertEquals(1, result.size());
            assertEquals("Test Product", result.get(0).name());
            assertEquals("http://example.com/thumb.jpg", result.get(0).thumbnailUrl());
        }

        @Test
        void getProductsByBrand_whenBrandNotFound_shouldThrowNotFoundException() {
            when(brandRepository.findBySlug("unknown-brand")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> productService.getProductsByBrand("unknown-brand"));
        }
    }

    @Nested
    class DeleteProductTest {

        @Test
        void deleteProduct_whenProductExists_shouldSetUnpublished() {
            product.setProductCategories(new ArrayList<>());
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            productService.deleteProduct(1L);

            assertFalse(product.isPublished());
            verify(productRepository).save(product);
        }

        @Test
        void deleteProduct_whenProductNotFound_shouldThrowNotFoundException() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.deleteProduct(999L));
        }

        @Test
        void deleteProduct_whenProductIsVariation_shouldDeleteOptionCombinations() {
            Product parent = Product.builder().id(100L).name("Parent").build();
            product.setParent(parent);

            List<com.yas.product.model.ProductOptionCombination> combinations = List.of(
                    com.yas.product.model.ProductOptionCombination.builder()
                            .product(product).value("Red").build()
            );

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productOptionCombinationRepository.findAllByProduct(product)).thenReturn(combinations);
            when(productRepository.save(any(Product.class))).thenReturn(product);

            productService.deleteProduct(1L);

            verify(productOptionCombinationRepository).deleteAll(combinations);
            assertFalse(product.isPublished());
        }
    }

    @Nested
    class GetProductSlugTest {

        @Test
        void getProductSlug_whenProductHasNoParent_shouldReturnOwnSlug() {
            product.setParent(null);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductSlugGetVm result = productService.getProductSlug(1L);

            assertEquals("test-product", result.slug());
            assertNull(result.productVariantId());
        }

        @Test
        void getProductSlug_whenProductHasParent_shouldReturnParentSlug() {
            Product parent = Product.builder().id(100L).slug("parent-product").build();
            product.setParent(parent);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductSlugGetVm result = productService.getProductSlug(1L);

            assertEquals("parent-product", result.slug());
            assertEquals(1L, result.productVariantId());
        }

        @Test
        void getProductSlug_whenProductNotFound_shouldThrowNotFoundException() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.getProductSlug(999L));
        }
    }

    @Nested
    class GetRelatedProductsBackofficeTest {

        @Test
        void getRelatedProductsBackoffice_whenProductHasRelated_shouldReturnList() {
            Product relatedProduct = Product.builder()
                    .id(2L).name("Related Product").slug("related-product")
                    .isAllowedToOrder(true).isPublished(true).isFeatured(false)
                    .isVisibleIndividually(true).price(50.0).taxClassId(1L)
                    .build();

            ProductRelated productRelated = ProductRelated.builder()
                    .product(product).relatedProduct(relatedProduct).build();
            product.setRelatedProducts(List.of(productRelated));

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            List<ProductListVm> result = productService.getRelatedProductsBackoffice(1L);

            assertEquals(1, result.size());
            assertEquals("Related Product", result.get(0).name());
        }

        @Test
        void getRelatedProductsBackoffice_whenNoRelated_shouldReturnEmptyList() {
            product.setRelatedProducts(new ArrayList<>());
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            List<ProductListVm> result = productService.getRelatedProductsBackoffice(1L);

            assertTrue(result.isEmpty());
        }

        @Test
        void getRelatedProductsBackoffice_whenProductNotFound_shouldThrowNotFoundException() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> productService.getRelatedProductsBackoffice(999L));
        }
    }

    @Nested
    class UpdateProductQuantityTest {

        @Test
        void updateProductQuantity_shouldUpdateStockQuantities() {
            Product product1 = Product.builder().id(1L).name("P1").build();
            Product product2 = Product.builder().id(2L).name("P2").build();

            var quantityVm1 = new com.yas.product.viewmodel.product.ProductQuantityPostVm(1L, 100L);
            var quantityVm2 = new com.yas.product.viewmodel.product.ProductQuantityPostVm(2L, 200L);

            when(productRepository.findAllByIdIn(List.of(1L, 2L)))
                    .thenReturn(List.of(product1, product2));

            productService.updateProductQuantity(List.of(quantityVm1, quantityVm2));

            verify(productRepository).saveAll(any());
        }
    }
}
