package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.ProductRelated;
import com.yas.product.model.enumeration.DimensionUnit;
import com.yas.product.model.enumeration.FilterExistInWhSelection;
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
import com.yas.product.viewmodel.product.ProductEsDetailVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductInfoVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
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
import org.springframework.data.domain.PageRequest;
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

    private Product sampleProduct;
    private Brand sampleBrand;

    @BeforeEach
    void setUp() {
        sampleBrand = new Brand();
        sampleBrand.setId(1L);
        sampleBrand.setName("Test Brand");
        sampleBrand.setSlug("test-brand");

        sampleProduct = Product.builder()
            .id(1L)
            .name("Test Product")
            .slug("test-product")
            .sku("SKU-001")
            .gtin("GTIN-001")
            .price(100.0)
            .isPublished(true)
            .isFeatured(false)
            .isAllowedToOrder(true)
            .isVisibleIndividually(true)
            .stockTrackingEnabled(false)
            .thumbnailMediaId(1L)
            .brand(sampleBrand)
            .weight(1.0)
            .dimensionUnit(DimensionUnit.CM)
            .length(10.0)
            .width(5.0)
            .height(3.0)
            .metaTitle("Meta Title")
            .metaKeyword("keyword")
            .metaDescription("Meta Description")
            .taxClassId(1L)
            .productCategories(new ArrayList<>())
            .productImages(new ArrayList<>())
            .products(new ArrayList<>())
            .relatedProducts(new ArrayList<>())
            .attributeValues(new ArrayList<>())
            .build();
    }

    // ==================== createProduct ====================

    @Nested
    class CreateProductTest {

        @Test
        void createProduct_whenLengthLessThanWidth_shouldThrowBadRequestException() {
            ProductPostVm postVm = new ProductPostVm(
                "Product", "product-slug", 1L, List.of(), "short", "desc", "spec",
                "SKU-NEW", "GTIN-NEW", 1.0, DimensionUnit.CM,
                5.0, 10.0, 3.0,  // length < width
                100.0, true, true, false, true, false,
                "meta", "key", "metaDesc", 1L, List.of(),
                List.of(), List.of(), List.of(), List.of(), 1L
            );

            assertThrows(BadRequestException.class, () -> productService.createProduct(postVm));
        }

        @Test
        void createProduct_whenSlugAlreadyExists_shouldThrowDuplicatedException() {
            ProductPostVm postVm = new ProductPostVm(
                "Product", "existing-slug", 1L, List.of(), "short", "desc", "spec",
                "SKU-NEW", "GTIN-NEW", 1.0, DimensionUnit.CM,
                10.0, 5.0, 3.0,
                100.0, true, true, false, true, false,
                "meta", "key", "metaDesc", 1L, List.of(),
                List.of(), List.of(), List.of(), List.of(), 1L
            );

            when(productRepository.findBySlugAndIsPublishedTrue("existing-slug"))
                .thenReturn(Optional.of(sampleProduct));

            assertThrows(DuplicatedException.class, () -> productService.createProduct(postVm));
        }

        @Test
        void createProduct_whenValidRequest_shouldReturnProductGetDetailVm() {
            ProductPostVm postVm = new ProductPostVm(
                "New Product", "new-product", 1L, List.of(1L), "short", "desc", "spec",
                "SKU-NEW", "GTIN-NEW", 1.0, DimensionUnit.CM,
                10.0, 5.0, 3.0,
                100.0, true, true, false, true, false,
                "meta", "key", "metaDesc", 1L, List.of(),
                List.of(), List.of(), List.of(), List.of(), 1L
            );

            Product savedProduct = Product.builder()
                .id(2L)
                .name("New Product")
                .slug("new-product")
                .build();

            when(productRepository.findBySlugAndIsPublishedTrue("new-product")).thenReturn(Optional.empty());
            when(productRepository.findBySkuAndIsPublishedTrue("SKU-NEW")).thenReturn(Optional.empty());
            when(productRepository.findByGtinAndIsPublishedTrue("GTIN-NEW")).thenReturn(Optional.empty());
            when(productRepository.findAllById(anyList())).thenReturn(List.of());
            when(brandRepository.findById(1L)).thenReturn(Optional.of(sampleBrand));

            Category category = new Category();
            category.setId(1L);
            category.setName("Category 1");
            when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));

            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
            when(productImageRepository.saveAll(anyList())).thenReturn(List.of());
            when(productCategoryRepository.saveAll(anyList())).thenReturn(List.of());

            ProductGetDetailVm result = productService.createProduct(postVm);

            assertNotNull(result);
            assertEquals("New Product", result.name());
            assertEquals("new-product", result.slug());
            verify(productRepository).save(any(Product.class));
        }
    }

    // ==================== getProductById ====================

    @Nested
    class GetProductByIdTest {

        @Test
        void getProductById_whenProductExists_shouldReturnProductDetailVm() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(mediaService.getMedia(anyLong()))
                .thenReturn(new NoFileMediaVm(1L, "caption", "file.jpg", "image/jpeg", "http://example.com/file.jpg"));

            ProductDetailVm result = productService.getProductById(1L);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("Test Product", result.name());
            assertEquals("test-product", result.slug());
            assertEquals(100.0, result.price());
            assertEquals(1L, result.brandId());
        }

        @Test
        void getProductById_whenProductNotFound_shouldThrowNotFoundException() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.getProductById(999L));
        }
    }

    // ==================== getProductsWithFilter ====================

    @Nested
    class GetProductsWithFilterTest {

        @Test
        void getProductsWithFilter_shouldReturnProductListGetVm() {
            List<Product> products = List.of(sampleProduct);
            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(productRepository.getProductsWithFilter(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(productPage);

            ProductListGetVm result = productService.getProductsWithFilter(0, 10, "Test", "");

            assertNotNull(result);
            assertEquals(1, result.productContent().size());
            assertEquals(0, result.pageNo());
            assertEquals(10, result.pageSize());
            assertEquals(1, result.totalElements());
        }
    }

    // ==================== getLatestProducts ====================

    @Nested
    class GetLatestProductsTest {

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
        void getLatestProducts_whenProductsExist_shouldReturnProducts() {
            when(productRepository.getLatestProducts(any(Pageable.class)))
                .thenReturn(List.of(sampleProduct));

            List<ProductListVm> result = productService.getLatestProducts(5);

            assertEquals(1, result.size());
            assertEquals("Test Product", result.get(0).name());
        }

        @Test
        void getLatestProducts_whenNoProducts_shouldReturnEmptyList() {
            when(productRepository.getLatestProducts(any(Pageable.class)))
                .thenReturn(List.of());

            List<ProductListVm> result = productService.getLatestProducts(5);

            assertTrue(result.isEmpty());
        }
    }

    // ==================== getProductsByBrand ====================

    @Nested
    class GetProductsByBrandTest {

        @Test
        void getProductsByBrand_whenBrandExists_shouldReturnProducts() {
            when(brandRepository.findBySlug("test-brand")).thenReturn(Optional.of(sampleBrand));
            when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(sampleBrand))
                .thenReturn(List.of(sampleProduct));
            when(mediaService.getMedia(anyLong()))
                .thenReturn(new NoFileMediaVm(1L, "caption", "file.jpg", "image/jpeg", "http://example.com/img.jpg"));

            List<ProductThumbnailVm> result = productService.getProductsByBrand("test-brand");

            assertEquals(1, result.size());
            assertEquals("Test Product", result.get(0).name());
        }

        @Test
        void getProductsByBrand_whenBrandNotFound_shouldThrowNotFoundException() {
            when(brandRepository.findBySlug("unknown-brand")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.getProductsByBrand("unknown-brand"));
        }
    }

    // ==================== getProductsFromCategory ====================

    @Nested
    class GetProductsFromCategoryTest {

        @Test
        void getProductsFromCategory_whenCategoryNotFound_shouldThrowNotFoundException() {
            when(categoryRepository.findBySlug("unknown")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                () -> productService.getProductsFromCategory(0, 10, "unknown"));
        }

        @Test
        void getProductsFromCategory_whenCategoryExists_shouldReturnProducts() {
            Category category = new Category();
            category.setId(1L);
            category.setName("Electronics");
            category.setSlug("electronics");

            ProductCategory productCategory = ProductCategory.builder()
                .product(sampleProduct)
                .category(category)
                .build();

            Page<ProductCategory> page = new PageImpl<>(List.of(productCategory), PageRequest.of(0, 10), 1);

            when(categoryRepository.findBySlug("electronics")).thenReturn(Optional.of(category));
            when(productCategoryRepository.findAllByCategory(any(Pageable.class), eq(category))).thenReturn(page);
            when(mediaService.getMedia(anyLong()))
                .thenReturn(new NoFileMediaVm(1L, "caption", "file.jpg", "image/jpeg", "http://example.com/img.jpg"));

            ProductListGetFromCategoryVm result = productService.getProductsFromCategory(0, 10, "electronics");

            assertNotNull(result);
            assertEquals(1, result.productContent().size());
        }
    }

    // ==================== getListFeaturedProducts ====================

    @Nested
    class GetListFeaturedProductsTest {

        @Test
        void getListFeaturedProducts_shouldReturnFeaturedProducts() {
            Page<Product> productPage = new PageImpl<>(List.of(sampleProduct), PageRequest.of(0, 10), 1);

            when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(productPage);
            when(mediaService.getMedia(anyLong()))
                .thenReturn(new NoFileMediaVm(1L, "caption", "file.jpg", "image/jpeg", "http://example.com/img.jpg"));

            ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 10);

            assertNotNull(result);
            assertEquals(1, result.productList().size());
            assertEquals(1, result.totalPage());
        }
    }

    // ==================== getFeaturedProductsById ====================

    @Nested
    class GetFeaturedProductsByIdTest {

        @Test
        void getFeaturedProductsById_shouldReturnThumbnailVms() {
            when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(sampleProduct));
            when(mediaService.getMedia(anyLong()))
                .thenReturn(new NoFileMediaVm(1L, "caption", "file.jpg", "image/jpeg", "http://example.com/img.jpg"));

            List<ProductThumbnailGetVm> result = productService.getFeaturedProductsById(List.of(1L));

            assertEquals(1, result.size());
            assertEquals("Test Product", result.get(0).name());
            assertEquals(100.0, result.get(0).price());
        }
    }

    // ==================== deleteProduct ====================

    @Nested
    class DeleteProductTest {

        @Test
        void deleteProduct_whenProductExists_shouldSetPublishedFalse() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

            productService.deleteProduct(1L);

            verify(productRepository).save(any(Product.class));
        }

        @Test
        void deleteProduct_whenProductNotFound_shouldThrowNotFoundException() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.deleteProduct(999L));
        }

        @Test
        void deleteProduct_whenProductHasParent_shouldDeleteOptionCombinations() {
            Product parentProduct = Product.builder().id(100L).name("Parent").build();
            sampleProduct.setParent(parentProduct);

            ProductOptionCombination combination = ProductOptionCombination.builder()
                .product(sampleProduct)
                .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productOptionCombinationRepository.findAllByProduct(sampleProduct))
                .thenReturn(List.of(combination));
            when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

            productService.deleteProduct(1L);

            verify(productOptionCombinationRepository).deleteAll(List.of(combination));
            verify(productRepository).save(any(Product.class));
        }
    }

    // ==================== getProductSlug ====================

    @Nested
    class GetProductSlugTest {

        @Test
        void getProductSlug_whenProductExists_shouldReturnSlug() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

            ProductSlugGetVm result = productService.getProductSlug(1L);

            assertEquals("test-product", result.slug());
        }

        @Test
        void getProductSlug_whenProductHasParent_shouldReturnParentSlug() {
            Product parentProduct = Product.builder().id(100L).slug("parent-slug").build();
            sampleProduct.setParent(parentProduct);

            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

            ProductSlugGetVm result = productService.getProductSlug(1L);

            assertEquals("parent-slug", result.slug());
            assertEquals(1L, result.productVariantId());
        }

        @Test
        void getProductSlug_whenProductNotFound_shouldThrowNotFoundException() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.getProductSlug(999L));
        }
    }

    // ==================== getProductEsDetailById ====================

    @Nested
    class GetProductEsDetailByIdTest {

        @Test
        void getProductEsDetailById_whenProductExists_shouldReturnEsDetail() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

            ProductEsDetailVm result = productService.getProductEsDetailById(1L);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("Test Product", result.name());
            assertEquals("test-product", result.slug());
            assertEquals(100.0, result.price());
            assertEquals("Test Brand", result.brand());
        }

        @Test
        void getProductEsDetailById_whenProductNotFound_shouldThrowNotFoundException() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.getProductEsDetailById(999L));
        }
    }

    // ==================== getProductsByMultiQuery ====================

    @Nested
    class GetProductsByMultiQueryTest {

        @Test
        void getProductsByMultiQuery_shouldReturnProductsGetVm() {
            Page<Product> productPage = new PageImpl<>(List.of(sampleProduct), PageRequest.of(0, 10), 1);

            when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(
                anyString(), anyString(), any(), any(), any(Pageable.class)))
                .thenReturn(productPage);
            when(mediaService.getMedia(anyLong()))
                .thenReturn(new NoFileMediaVm(1L, "caption", "file.jpg", "image/jpeg", "http://example.com/img.jpg"));

            ProductsGetVm result = productService.getProductsByMultiQuery(0, 10, "Test", "", null, null);

            assertNotNull(result);
            assertEquals(1, result.productContent().size());
            assertEquals(0, result.pageNo());
        }
    }

    // ==================== getProductVariationsByParentId ====================

    @Nested
    class GetProductVariationsByParentIdTest {

        @Test
        void getProductVariationsByParentId_whenProductNotFound_shouldThrowNotFoundException() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                () -> productService.getProductVariationsByParentId(999L));
        }

        @Test
        void getProductVariationsByParentId_whenProductHasNoOptions_shouldReturnEmptyList() {
            sampleProduct.setHasOptions(false);
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

            var result = productService.getProductVariationsByParentId(1L);

            assertTrue(result.isEmpty());
        }
    }

    // ==================== exportProducts ====================

    @Nested
    class ExportProductsTest {

        @Test
        void exportProducts_shouldReturnExportingDetails() {
            when(productRepository.getExportingProducts(anyString(), anyString()))
                .thenReturn(List.of(sampleProduct));

            var result = productService.exportProducts("Test", "");

            assertEquals(1, result.size());
            assertEquals("Test Product", result.get(0).name());
        }
    }

    // ==================== updateProductQuantity ====================

    @Nested
    class UpdateProductQuantityTest {

        @Test
        void updateProductQuantity_shouldUpdateStockQuantity() {
            ProductQuantityPostVm quantityVm = new ProductQuantityPostVm(1L, 50L);

            when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(sampleProduct));
            when(productRepository.saveAll(anyList())).thenReturn(List.of(sampleProduct));

            productService.updateProductQuantity(List.of(quantityVm));

            verify(productRepository).saveAll(anyList());
        }
    }

    // ==================== subtractStockQuantity ====================

    @Nested
    class SubtractStockQuantityTest {

        @Test
        void subtractStockQuantity_whenTrackingEnabled_shouldSubtract() {
            sampleProduct.setStockTrackingEnabled(true);
            sampleProduct.setStockQuantity(100L);

            ProductQuantityPutVm quantityPutVm = new ProductQuantityPutVm(1L, 10L);

            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(sampleProduct));
            when(productRepository.saveAll(anyList())).thenReturn(List.of(sampleProduct));

            productService.subtractStockQuantity(List.of(quantityPutVm));

            verify(productRepository).saveAll(anyList());
        }
    }

    // ==================== restoreStockQuantity ====================

    @Nested
    class RestoreStockQuantityTest {

        @Test
        void restoreStockQuantity_whenTrackingEnabled_shouldRestore() {
            sampleProduct.setStockTrackingEnabled(true);
            sampleProduct.setStockQuantity(90L);

            ProductQuantityPutVm quantityPutVm = new ProductQuantityPutVm(1L, 10L);

            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(sampleProduct));
            when(productRepository.saveAll(anyList())).thenReturn(List.of(sampleProduct));

            productService.restoreStockQuantity(List.of(quantityPutVm));

            verify(productRepository).saveAll(anyList());
        }
    }

    // ==================== getProductByIds ====================

    @Nested
    class GetProductByIdsTest {

        @Test
        void getProductByIds_shouldReturnProductList() {
            when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(sampleProduct));

            List<ProductListVm> result = productService.getProductByIds(List.of(1L));

            assertEquals(1, result.size());
            assertEquals("Test Product", result.get(0).name());
        }

        @Test
        void getProductByIds_whenNoProducts_shouldReturnEmptyList() {
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of());

            List<ProductListVm> result = productService.getProductByIds(List.of(999L));

            assertTrue(result.isEmpty());
        }
    }

    // ==================== getProductByCategoryIds ====================

    @Nested
    class GetProductByCategoryIdsTest {

        @Test
        void getProductByCategoryIds_shouldReturnProducts() {
            when(productRepository.findByCategoryIdsIn(List.of(1L))).thenReturn(List.of(sampleProduct));

            List<ProductListVm> result = productService.getProductByCategoryIds(List.of(1L));

            assertEquals(1, result.size());
        }
    }

    // ==================== getProductByBrandIds ====================

    @Nested
    class GetProductByBrandIdsTest {

        @Test
        void getProductByBrandIds_shouldReturnProducts() {
            when(productRepository.findByBrandIdsIn(List.of(1L))).thenReturn(List.of(sampleProduct));

            List<ProductListVm> result = productService.getProductByBrandIds(List.of(1L));

            assertEquals(1, result.size());
        }
    }

    // ==================== getProductsForWarehouse ====================

    @Nested
    class GetProductsForWarehouseTest {

        @Test
        void getProductsForWarehouse_shouldReturnProductInfoList() {
            when(productRepository.findProductForWarehouse(
                anyString(), anyString(), anyList(), anyString()))
                .thenReturn(List.of(sampleProduct));

            List<ProductInfoVm> result = productService.getProductsForWarehouse(
                "Test", "SKU", List.of(1L), FilterExistInWhSelection.ALL);

            assertEquals(1, result.size());
            assertEquals("Test Product", result.get(0).name());
        }
    }

    // ==================== getRelatedProductsBackoffice ====================

    @Nested
    class GetRelatedProductsBackofficeTest {

        @Test
        void getRelatedProductsBackoffice_whenProductNotFound_shouldThrowNotFoundException() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                () -> productService.getRelatedProductsBackoffice(999L));
        }

        @Test
        void getRelatedProductsBackoffice_whenProductExists_shouldReturnRelatedProducts() {
            Product relatedProduct = Product.builder()
                .id(2L)
                .name("Related Product")
                .slug("related-product")
                .price(50.0)
                .isAllowedToOrder(true)
                .isPublished(true)
                .isFeatured(false)
                .isVisibleIndividually(true)
                .build();

            ProductRelated productRelated = ProductRelated.builder()
                .product(sampleProduct)
                .relatedProduct(relatedProduct)
                .build();

            sampleProduct.setRelatedProducts(List.of(productRelated));

            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

            List<ProductListVm> result = productService.getRelatedProductsBackoffice(1L);

            assertEquals(1, result.size());
            assertEquals("Related Product", result.get(0).name());
        }
    }

    // ==================== getRelatedProductsStorefront ====================

    @Nested
    class GetRelatedProductsStorefrontTest {

        @Test
        void getRelatedProductsStorefront_whenProductNotFound_shouldThrowNotFoundException() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                () -> productService.getRelatedProductsStorefront(999L, 0, 10));
        }

        @Test
        void getRelatedProductsStorefront_shouldReturnRelatedProducts() {
            Product relatedProduct = Product.builder()
                .id(2L)
                .name("Related")
                .slug("related")
                .price(50.0)
                .isPublished(true)
                .thumbnailMediaId(2L)
                .build();

            ProductRelated productRelated = ProductRelated.builder()
                .product(sampleProduct)
                .relatedProduct(relatedProduct)
                .build();

            Page<ProductRelated> page = new PageImpl<>(List.of(productRelated), PageRequest.of(0, 10), 1);

            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productRelatedRepository.findAllByProduct(any(Product.class), any(Pageable.class)))
                .thenReturn(page);
            when(mediaService.getMedia(anyLong()))
                .thenReturn(new NoFileMediaVm(2L, "caption", "file.jpg", "image/jpeg", "http://example.com/img.jpg"));

            ProductsGetVm result = productService.getRelatedProductsStorefront(1L, 0, 10);

            assertNotNull(result);
            assertEquals(1, result.productContent().size());
        }
    }

    // ==================== updateProduct ====================

    @Nested
    class UpdateProductTest {

        @Test
        void updateProduct_whenProductNotFound_shouldThrowNotFoundException() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                () -> productService.updateProduct(999L, null));
        }
    }

    // ==================== getProductCheckoutList ====================

    @Nested
    class GetProductCheckoutListTest {

        @Test
        void getProductCheckoutList_shouldReturnCheckoutList() {
            Page<Product> productPage = new PageImpl<>(List.of(sampleProduct), PageRequest.of(0, 10), 1);

            when(productRepository.findAllPublishedProductsByIds(anyList(), any(Pageable.class)))
                .thenReturn(productPage);
            when(mediaService.getMedia(anyLong()))
                .thenReturn(new NoFileMediaVm(1L, "caption", "file.jpg", "image/jpeg", "http://example.com/img.jpg"));

            var result = productService.getProductCheckoutList(0, 10, List.of(1L));

            assertNotNull(result);
            assertEquals(1, result.productCheckoutListVms().size());
            assertEquals(0, result.pageNo());
        }
    }
}
