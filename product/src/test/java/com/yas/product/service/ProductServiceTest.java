package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.ProductRelated;
import com.yas.product.model.attribute.ProductAttributeValue;
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
import com.yas.product.viewmodel.product.ProductDetailGetVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductEsDetailVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductGetCheckoutListVm;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductVariationGetVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    private static final Long PRODUCT_ID = 1L;
    private static final Long BRAND_ID = 10L;
    private static final Long CATEGORY_ID = 20L;
    private static final Long THUMBNAIL_ID = 100L;
    private static final String PRODUCT_NAME = "Test Product";
    private static final String SLUG = "test-product";
    private static final String SKU = "SKU-001";
    private static final String BRAND_SLUG = "test-brand";
    private static final String CATEGORY_SLUG = "test-cat";
    private static final String THUMB_URL = "http://media/t.jpg";

    @Mock private ProductRepository productRepository;
    @Mock private MediaService mediaService;
    @Mock private BrandRepository brandRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductCategoryRepository productCategoryRepository;
    @Mock private ProductImageRepository productImageRepository;
    @Mock private ProductOptionRepository productOptionRepository;
    @Mock private ProductOptionValueRepository productOptionValueRepository;
    @Mock private ProductOptionCombinationRepository productOptionCombinationRepository;
    @Mock private ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    private ProductService productService;

    private Product buildProduct() {
        Brand brand = new Brand();
        brand.setId(BRAND_ID);
        brand.setName("Brand");
        brand.setSlug(BRAND_SLUG);
        brand.setProducts(new ArrayList<>());
        Category cat = new Category();
        cat.setId(CATEGORY_ID);
        cat.setName("Cat");
        ProductCategory pc = ProductCategory.builder().product(null).category(cat).build();
        Product p = Product.builder()
            .id(PRODUCT_ID).name(PRODUCT_NAME).slug(SLUG).sku(SKU)
            .price(99.0).isPublished(true).isFeatured(true)
            .isAllowedToOrder(true).isVisibleIndividually(true)
            .hasOptions(false).thumbnailMediaId(THUMBNAIL_ID)
            .brand(brand).stockTrackingEnabled(true).stockQuantity(50L)
            .build();
        pc = ProductCategory.builder().product(p).category(cat).build();
        p.setProductCategories(List.of(pc));
        p.setProductImages(new ArrayList<>());
        p.setAttributeValues(new ArrayList<>());
        p.setProducts(new ArrayList<>());
        p.setRelatedProducts(new ArrayList<>());
        return p;
    }

    private NoFileMediaVm mediaVm() {
        return new NoFileMediaVm(1L, "", "", "", THUMB_URL);
    }

    @Nested
    class GetProductByIdTest {
        @Test
        void testGetProductById_whenExists_shouldReturn() {
            Product p = buildProduct();
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(p));
            when(mediaService.getMedia(anyLong())).thenReturn(mediaVm());
            ProductDetailVm r = productService.getProductById(PRODUCT_ID);
            assertThat(r).isNotNull();
            assertThat(r.id()).isEqualTo(PRODUCT_ID);
            assertThat(r.name()).isEqualTo(PRODUCT_NAME);
        }

        @Test
        void testGetProductById_whenNotFound_shouldThrow() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> productService.getProductById(PRODUCT_ID));
        }

        @Test
        void testGetProductById_whenNoBrand_shouldReturnNullBrandId() {
            Product p = buildProduct();
            p.setBrand(null);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(p));
            when(mediaService.getMedia(anyLong())).thenReturn(mediaVm());
            ProductDetailVm r = productService.getProductById(PRODUCT_ID);
            assertThat(r.brandId()).isNull();
        }

        @Test
        void testGetProductById_whenHasImages_shouldReturnImages() {
            Product p = buildProduct();
            p.setProductImages(List.of(ProductImage.builder().imageId(200L).product(p).build()));
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(p));
            when(mediaService.getMedia(anyLong())).thenReturn(mediaVm());
            ProductDetailVm r = productService.getProductById(PRODUCT_ID);
            assertThat(r.productImageMedias()).hasSize(1);
        }
    }

    @Nested
    class GetProductsWithFilterTest {
        @Test
        void testGetProductsWithFilter_shouldReturn() {
            Product p = buildProduct();
            Page<Product> page = new PageImpl<>(List.of(p), PageRequest.of(0, 10), 1);
            when(productRepository.getProductsWithFilter(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(page);
            ProductListGetVm r = productService.getProductsWithFilter(0, 10, "test", "brand");
            assertThat(r.productContent()).hasSize(1);
        }

        @Test
        void testGetProductsWithFilter_whenEmpty_shouldReturnEmpty() {
            Page<Product> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
            when(productRepository.getProductsWithFilter(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(page);
            ProductListGetVm r = productService.getProductsWithFilter(0, 10, "x", "y");
            assertThat(r.productContent()).isEmpty();
        }
    }

    @Nested
    class GetLatestProductsTest {
        @Test
        void testGetLatestProducts_whenCountPositive_shouldReturn() {
            when(productRepository.getLatestProducts(any(Pageable.class)))
                .thenReturn(List.of(buildProduct()));
            List<ProductListVm> r = productService.getLatestProducts(5);
            assertThat(r).hasSize(1);
        }

        @Test
        void testGetLatestProducts_whenCountZero_shouldReturnEmpty() {
            List<ProductListVm> r = productService.getLatestProducts(0);
            assertThat(r).isEmpty();
        }

        @Test
        void testGetLatestProducts_whenNegative_shouldReturnEmpty() {
            List<ProductListVm> r = productService.getLatestProducts(-1);
            assertThat(r).isEmpty();
        }

        @Test
        void testGetLatestProducts_whenRepoEmpty_shouldReturnEmpty() {
            when(productRepository.getLatestProducts(any(Pageable.class))).thenReturn(List.of());
            List<ProductListVm> r = productService.getLatestProducts(5);
            assertThat(r).isEmpty();
        }
    }

    @Nested
    class DeleteProductTest {
        @Test
        void testDeleteProduct_whenExists_shouldSetUnpublished() {
            Product p = buildProduct();
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(p));
            productService.deleteProduct(PRODUCT_ID);
            assertThat(p.isPublished()).isFalse();
            verify(productRepository).save(p);
        }

        @Test
        void testDeleteProduct_whenNotFound_shouldThrow() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> productService.deleteProduct(PRODUCT_ID));
        }

        @Test
        void testDeleteProduct_whenHasParent_shouldDeleteCombinations() {
            Product parent = buildProduct();
            Product child = buildProduct();
            child.setId(2L);
            child.setParent(parent);
            when(productRepository.findById(2L)).thenReturn(Optional.of(child));
            when(productOptionCombinationRepository.findAllByProduct(child))
                .thenReturn(List.of(new ProductOptionCombination()));
            productService.deleteProduct(2L);
            verify(productOptionCombinationRepository).deleteAll(anyList());
            verify(productRepository).save(child);
        }
    }

    @Nested
    class GetProductsByBrandTest {
        @Test
        void testGetProductsByBrand_whenBrandExists_shouldReturn() {
            Brand brand = new Brand();
            brand.setId(BRAND_ID);
            brand.setSlug(BRAND_SLUG);
            when(brandRepository.findBySlug(BRAND_SLUG)).thenReturn(Optional.of(brand));
            Product p = buildProduct();
            when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand))
                .thenReturn(List.of(p));
            when(mediaService.getMedia(anyLong())).thenReturn(mediaVm());
            List<ProductThumbnailVm> r = productService.getProductsByBrand(BRAND_SLUG);
            assertThat(r).hasSize(1);
        }

        @Test
        void testGetProductsByBrand_whenBrandNotFound_shouldThrow() {
            when(brandRepository.findBySlug(BRAND_SLUG)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> productService.getProductsByBrand(BRAND_SLUG));
        }
    }

    @Nested
    class GetProductsFromCategoryTest {
        @Test
        void testGetProductsFromCategory_whenCategoryExists_shouldReturn() {
            Category cat = new Category();
            cat.setId(CATEGORY_ID);
            cat.setSlug(CATEGORY_SLUG);
            Product p = buildProduct();
            ProductCategory pc = ProductCategory.builder().product(p).category(cat).build();
            Page<ProductCategory> page = new PageImpl<>(List.of(pc), PageRequest.of(0, 10), 1);
            when(categoryRepository.findBySlug(CATEGORY_SLUG)).thenReturn(Optional.of(cat));
            when(productCategoryRepository.findAllByCategory(any(Pageable.class), any(Category.class)))
                .thenReturn(page);
            when(mediaService.getMedia(anyLong())).thenReturn(mediaVm());
            ProductListGetFromCategoryVm r = productService.getProductsFromCategory(0, 10, CATEGORY_SLUG);
            assertThat(r.productContent()).hasSize(1);
        }

        @Test
        void testGetProductsFromCategory_whenCategoryNotFound_shouldThrow() {
            when(categoryRepository.findBySlug(CATEGORY_SLUG)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class,
                () -> productService.getProductsFromCategory(0, 10, CATEGORY_SLUG));
        }
    }

    @Nested
    class GetListFeaturedProductsTest {
        @Test
        void testGetListFeaturedProducts_shouldReturn() {
            Product p = buildProduct();
            Page<Product> page = new PageImpl<>(List.of(p), PageRequest.of(0, 10), 1);
            when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(page);
            when(mediaService.getMedia(anyLong())).thenReturn(mediaVm());
            ProductFeatureGetVm r = productService.getListFeaturedProducts(0, 10);
            assertThat(r.productList()).hasSize(1);
        }
    }

    @Nested
    class GetProductDetailTest {
        @Test
        void testGetProductDetail_whenExists_shouldReturn() {
            Product p = buildProduct();
            when(productRepository.findBySlugAndIsPublishedTrue(SLUG)).thenReturn(Optional.of(p));
            when(mediaService.getMedia(anyLong())).thenReturn(mediaVm());
            ProductDetailGetVm r = productService.getProductDetail(SLUG);
            assertThat(r).isNotNull();
            assertThat(r.name()).isEqualTo(PRODUCT_NAME);
        }

        @Test
        void testGetProductDetail_whenNotFound_shouldThrow() {
            when(productRepository.findBySlugAndIsPublishedTrue(SLUG)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> productService.getProductDetail(SLUG));
        }
    }

    @Nested
    class GetProductSlugTest {
        @Test
        void testGetProductSlug_whenNoParent_shouldReturnOwnSlug() {
            Product p = buildProduct();
            p.setParent(null);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(p));
            ProductSlugGetVm r = productService.getProductSlug(PRODUCT_ID);
            assertThat(r.slug()).isEqualTo(SLUG);
            assertThat(r.productVariantId()).isNull();
        }

        @Test
        void testGetProductSlug_whenHasParent_shouldReturnParentSlug() {
            Product parent = buildProduct();
            parent.setSlug("parent-slug");
            Product child = buildProduct();
            child.setId(2L);
            child.setParent(parent);
            when(productRepository.findById(2L)).thenReturn(Optional.of(child));
            ProductSlugGetVm r = productService.getProductSlug(2L);
            assertThat(r.slug()).isEqualTo("parent-slug");
        }

        @Test
        void testGetProductSlug_whenNotFound_shouldThrow() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> productService.getProductSlug(PRODUCT_ID));
        }
    }

    @Nested
    class GetProductEsDetailByIdTest {
        @Test
        void testGetProductEsDetailById_whenExists_shouldReturn() {
            Product p = buildProduct();
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(p));
            ProductEsDetailVm r = productService.getProductEsDetailById(PRODUCT_ID);
            assertThat(r.id()).isEqualTo(PRODUCT_ID);
            assertThat(r.name()).isEqualTo(PRODUCT_NAME);
        }

        @Test
        void testGetProductEsDetailById_whenNoBrand_shouldReturnNullBrand() {
            Product p = buildProduct();
            p.setBrand(null);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(p));
            ProductEsDetailVm r = productService.getProductEsDetailById(PRODUCT_ID);
            assertThat(r.brand()).isNull();
        }

        @Test
        void testGetProductEsDetailById_whenNotFound_shouldThrow() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> productService.getProductEsDetailById(PRODUCT_ID));
        }
    }

    @Nested
    class GetRelatedProductsBackofficeTest {
        @Test
        void testGetRelatedProductsBackoffice_whenExists_shouldReturn() {
            Product p = buildProduct();
            Product related = buildProduct();
            related.setId(2L);
            related.setName("Related");
            ProductRelated pr = ProductRelated.builder().product(p).relatedProduct(related).build();
            p.setRelatedProducts(List.of(pr));
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(p));
            List<ProductListVm> r = productService.getRelatedProductsBackoffice(PRODUCT_ID);
            assertThat(r).hasSize(1);
        }

        @Test
        void testGetRelatedProductsBackoffice_whenNotFound_shouldThrow() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class,
                () -> productService.getRelatedProductsBackoffice(PRODUCT_ID));
        }
    }

    @Nested
    class GetRelatedProductsStorefrontTest {
        @Test
        void testGetRelatedProductsStorefront_whenExists_shouldReturn() {
            Product p = buildProduct();
            Product related = buildProduct();
            related.setId(2L);
            ProductRelated pr = ProductRelated.builder().product(p).relatedProduct(related).build();
            Page<ProductRelated> page = new PageImpl<>(List.of(pr), PageRequest.of(0, 10), 1);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(p));
            when(productRelatedRepository.findAllByProduct(any(Product.class), any(Pageable.class)))
                .thenReturn(page);
            when(mediaService.getMedia(anyLong())).thenReturn(mediaVm());
            ProductsGetVm r = productService.getRelatedProductsStorefront(PRODUCT_ID, 0, 10);
            assertThat(r.productContent()).hasSize(1);
        }

        @Test
        void testGetRelatedProductsStorefront_whenNotFound_shouldThrow() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class,
                () -> productService.getRelatedProductsStorefront(PRODUCT_ID, 0, 10));
        }
    }

    @Nested
    class GetProductsByMultiQueryTest {
        @Test
        void testGetProductsByMultiQuery_shouldReturn() {
            Product p = buildProduct();
            Page<Product> page = new PageImpl<>(List.of(p), PageRequest.of(0, 10), 1);
            when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(
                anyString(), anyString(), any(), any(), any(Pageable.class))).thenReturn(page);
            when(mediaService.getMedia(anyLong())).thenReturn(mediaVm());
            ProductsGetVm r = productService.getProductsByMultiQuery(0, 10, "test", "cat", 0.0, 100.0);
            assertThat(r.productContent()).hasSize(1);
        }
    }

    @Nested
    class GetProductVariationsByParentIdTest {
        @Test
        void testGetVariations_whenHasOptions_shouldReturn() {
            Product p = buildProduct();
            p.setHasOptions(true);
            Product var1 = Product.builder().id(2L).name("V1").slug("v1").sku("VS")
                .price(50.0).isPublished(true).parent(p).thumbnailMediaId(THUMBNAIL_ID).build();
            var1.setProductImages(new ArrayList<>());
            p.setProducts(List.of(var1));
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(p));
            when(productOptionCombinationRepository.findAllByProduct(var1)).thenReturn(List.of());
            when(mediaService.getMedia(anyLong())).thenReturn(mediaVm());
            List<ProductVariationGetVm> r = productService.getProductVariationsByParentId(PRODUCT_ID);
            assertThat(r).hasSize(1);
        }

        @Test
        void testGetVariations_whenNoOptions_shouldReturnEmpty() {
            Product p = buildProduct();
            p.setHasOptions(false);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(p));
            List<ProductVariationGetVm> r = productService.getProductVariationsByParentId(PRODUCT_ID);
            assertThat(r).isEmpty();
        }

        @Test
        void testGetVariations_whenNotFound_shouldThrow() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class,
                () -> productService.getProductVariationsByParentId(PRODUCT_ID));
        }
    }

    @Nested
    class ExportProductsTest {
        @Test
        void testExportProducts_shouldReturn() {
            when(productRepository.getExportingProducts(anyString(), anyString()))
                .thenReturn(List.of(buildProduct()));
            var r = productService.exportProducts("test", "brand");
            assertThat(r).hasSize(1);
        }
    }

    @Nested
    class GetProductsForWarehouseTest {
        @Test
        void testGetProductsForWarehouse_shouldReturn() {
            when(productRepository.findProductForWarehouse(anyString(), anyString(), anyList(), anyString()))
                .thenReturn(List.of(buildProduct()));
            var r = productService.getProductsForWarehouse("n", "s", List.of(1L), FilterExistInWhSelection.ALL);
            assertThat(r).hasSize(1);
        }
    }

    @Nested
    class UpdateProductQuantityTest {
        @Test
        void testUpdateProductQuantity_shouldUpdateStock() {
            Product p = buildProduct();
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(p));
            ProductQuantityPostVm vm = new ProductQuantityPostVm(PRODUCT_ID, 25L);
            productService.updateProductQuantity(List.of(vm));
            verify(productRepository).saveAll(anyList());
        }
    }

    @Nested
    class SubtractStockQuantityTest {
        @Test
        void testSubtractStockQuantity_shouldSubtract() {
            Product p = buildProduct();
            p.setStockQuantity(100L);
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(p));
            productService.subtractStockQuantity(List.of(new ProductQuantityPutVm(PRODUCT_ID, 10L)));
            verify(productRepository).saveAll(anyList());
        }
    }

    @Nested
    class RestoreStockQuantityTest {
        @Test
        void testRestoreStockQuantity_shouldRestore() {
            Product p = buildProduct();
            p.setStockQuantity(90L);
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(p));
            productService.restoreStockQuantity(List.of(new ProductQuantityPutVm(PRODUCT_ID, 10L)));
            verify(productRepository).saveAll(anyList());
        }
    }

    @Nested
    class GetProductByIdsTest {
        @Test
        void testGetProductByIds_shouldReturn() {
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(buildProduct()));
            List<ProductListVm> r = productService.getProductByIds(List.of(PRODUCT_ID));
            assertThat(r).hasSize(1);
        }

        @Test
        void testGetProductByIds_whenEmpty_shouldReturnEmpty() {
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of());
            List<ProductListVm> r = productService.getProductByIds(List.of());
            assertThat(r).isEmpty();
        }
    }

    @Nested
    class GetProductByCategoryIdsTest {
        @Test
        void testGetProductByCategoryIds_shouldReturn() {
            when(productRepository.findByCategoryIdsIn(anyList())).thenReturn(List.of(buildProduct()));
            var r = productService.getProductByCategoryIds(List.of(CATEGORY_ID));
            assertThat(r).hasSize(1);
        }
    }

    @Nested
    class GetProductByBrandIdsTest {
        @Test
        void testGetProductByBrandIds_shouldReturn() {
            when(productRepository.findByBrandIdsIn(anyList())).thenReturn(List.of(buildProduct()));
            var r = productService.getProductByBrandIds(List.of(BRAND_ID));
            assertThat(r).hasSize(1);
        }
    }

    @Nested
    class GetFeaturedProductsByIdTest {
        @Test
        void testGetFeaturedProductsById_shouldReturn() {
            Product p = buildProduct();
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(p));
            when(mediaService.getMedia(anyLong())).thenReturn(mediaVm());
            List<ProductThumbnailGetVm> r = productService.getFeaturedProductsById(List.of(PRODUCT_ID));
            assertThat(r).hasSize(1);
        }
    }

    @Nested
    class GetProductCheckoutListTest {
        @Test
        void testGetProductCheckoutList_shouldReturn() {
            Product p = buildProduct();
            Page<Product> page = new PageImpl<>(List.of(p), PageRequest.of(0, 10), 1);
            when(productRepository.findAllPublishedProductsByIds(anyList(), any(Pageable.class)))
                .thenReturn(page);
            when(mediaService.getMedia(anyLong())).thenReturn(mediaVm());
            ProductGetCheckoutListVm r = productService.getProductCheckoutList(0, 10, List.of(PRODUCT_ID));
            assertThat(r.productCheckoutListVms()).hasSize(1);
        }
    }

    @Nested
    class CreateProductTest {
        @Test
        void testCreateProduct_whenValidNoVariations_shouldCreate() {
            ProductPostVm vm = new ProductPostVm(
                PRODUCT_NAME, SLUG, BRAND_ID, List.of(CATEGORY_ID),
                "short", "desc", "spec", SKU, "gtin1",
                10.0, null, 20.0, 10.0, 5.0,
                99.0, true, true, true, true, true,
                "metaTitle", "metaKey", "metaDesc",
                THUMBNAIL_ID, List.of(200L),
                List.of(), List.of(), List.of(),
                List.of(), 1L
            );
            Brand brand = new Brand();
            brand.setId(BRAND_ID);

            when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
            when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
            when(productRepository.findByGtinAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
            when(productRepository.findAllById(anyList())).thenReturn(List.of());
            Product saved = buildProduct();
            when(productRepository.save(any(Product.class))).thenReturn(saved);
            when(brandRepository.findById(BRAND_ID)).thenReturn(Optional.of(brand));
            when(productImageRepository.saveAll(anyList())).thenReturn(List.of());

            ProductGetDetailVm result = productService.createProduct(vm);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(PRODUCT_NAME);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        void testCreateProduct_whenBrandNotFound_shouldThrow() {
            ProductPostVm vm = new ProductPostVm(
                PRODUCT_NAME, SLUG, 999L, List.of(),
                "short", "desc", "spec", SKU, "",
                10.0, null, 20.0, 10.0, 5.0,
                99.0, true, true, true, true, true,
                "metaTitle", "metaKey", "metaDesc",
                THUMBNAIL_ID, List.of(),
                List.of(), List.of(), List.of(),
                List.of(), 1L
            );
            when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
            when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
            when(productRepository.findAllById(anyList())).thenReturn(List.of());
            when(brandRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.createProduct(vm));
        }

        @Test
        void testCreateProduct_whenDuplicateSlug_shouldThrow() {
            ProductPostVm vm = new ProductPostVm(
                PRODUCT_NAME, SLUG, null, List.of(),
                "short", "desc", "spec", SKU, "",
                10.0, null, 20.0, 10.0, 5.0,
                99.0, true, true, true, true, true,
                "metaTitle", "metaKey", "metaDesc",
                null, List.of(),
                List.of(), List.of(), List.of(),
                List.of(), 1L
            );
            when(productRepository.findBySlugAndIsPublishedTrue(SLUG)).thenReturn(Optional.of(buildProduct()));

            assertThrows(DuplicatedException.class, () -> productService.createProduct(vm));
        }

        @Test
        void testCreateProduct_whenLengthLessThanWidth_shouldThrow() {
            ProductPostVm vm = new ProductPostVm(
                PRODUCT_NAME, SLUG, null, List.of(),
                "short", "desc", "spec", SKU, "",
                10.0, null, 5.0, 20.0, 5.0,
                99.0, true, true, true, true, true,
                "metaTitle", "metaKey", "metaDesc",
                null, List.of(),
                List.of(), List.of(), List.of(),
                List.of(), 1L
            );

            assertThrows(BadRequestException.class, () -> productService.createProduct(vm));
        }

        @Test
        void testCreateProduct_whenDuplicateSku_shouldThrow() {
            ProductPostVm vm = new ProductPostVm(
                PRODUCT_NAME, "unique-slug", null, List.of(),
                "short", "desc", "spec", SKU, "",
                10.0, null, 20.0, 10.0, 5.0,
                99.0, true, true, true, true, true,
                "metaTitle", "metaKey", "metaDesc",
                null, List.of(),
                List.of(), List.of(), List.of(),
                List.of(), 1L
            );
            when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
            when(productRepository.findBySkuAndIsPublishedTrue(SKU)).thenReturn(Optional.of(buildProduct()));

            assertThrows(DuplicatedException.class, () -> productService.createProduct(vm));
        }

        @Test
        void testCreateProduct_withRelatedProducts_shouldSaveRelations() {
            ProductPostVm vm = new ProductPostVm(
                PRODUCT_NAME, SLUG, BRAND_ID, List.of(CATEGORY_ID),
                "short", "desc", "spec", SKU, "",
                10.0, null, 20.0, 10.0, 5.0,
                99.0, true, true, true, true, true,
                "metaTitle", "metaKey", "metaDesc",
                THUMBNAIL_ID, List.of(),
                List.of(), List.of(), List.of(),
                List.of(2L), 1L
            );
            Brand brand = new Brand();
            brand.setId(BRAND_ID);
            Product related = buildProduct();
            related.setId(2L);

            when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
            when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
            when(productRepository.findAllById(anyList())).thenReturn(List.of(related));
            Product saved = buildProduct();
            when(productRepository.save(any(Product.class))).thenReturn(saved);
            when(brandRepository.findById(BRAND_ID)).thenReturn(Optional.of(brand));
            when(productImageRepository.saveAll(anyList())).thenReturn(List.of());
            when(productRelatedRepository.saveAll(anyList())).thenReturn(List.of());

            ProductGetDetailVm result = productService.createProduct(vm);

            assertThat(result).isNotNull();
            verify(productRelatedRepository).saveAll(anyList());
        }
    }

    @Nested
    class SetProductImagesTest {
        @Test
        void testSetProductImages_whenEmptyList_shouldDeleteExisting() {
            Product p = buildProduct();
            List<ProductImage> result = productService.setProductImages(List.of(), p);
            assertThat(result).isEmpty();
            verify(productImageRepository).deleteByProductId(PRODUCT_ID);
        }

        @Test
        void testSetProductImages_whenNullList_shouldDeleteExisting() {
            Product p = buildProduct();
            List<ProductImage> result = productService.setProductImages(null, p);
            assertThat(result).isEmpty();
            verify(productImageRepository).deleteByProductId(PRODUCT_ID);
        }

        @Test
        void testSetProductImages_whenNoExistingImages_shouldCreateAll() {
            Product p = buildProduct();
            p.setProductImages(null);
            List<ProductImage> result = productService.setProductImages(List.of(100L, 200L), p);
            assertThat(result).hasSize(2);
        }

        @Test
        void testSetProductImages_whenHasExistingImages_shouldOnlyAddNew() {
            Product p = buildProduct();
            ProductImage existing = ProductImage.builder().imageId(100L).product(p).build();
            p.setProductImages(List.of(existing));
            List<ProductImage> result = productService.setProductImages(List.of(100L, 200L), p);
            assertThat(result).hasSize(1);
        }
    }
}
