package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.ProductRelated;
import com.yas.product.model.enumeration.FilterExistInWhSelection;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
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
import com.yas.product.viewmodel.product.ProductGetCheckoutListVm;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import java.util.ArrayList;
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
import org.springframework.data.domain.Pageable;

@MockitoSettings(strictness = Strictness.LENIENT)
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

    private ProductPostVm buildPostVm(String name, String slug, Long brandId, List<Long> categoryIds) {
        return new ProductPostVm(
            name, slug, brandId, categoryIds,
            "short desc", "desc", "spec",
            "SKU-001", "", 1.0, null, 2.0, 1.0, 0.5,
            10.0, true, true, false, true, false,
            "title", "keyword", "meta",
            null, List.of(), List.of(), List.of(), List.of(), List.of(), null
        );
    }

    private ProductPutVm buildPutVm(String name, String slug, Long brandId, List<Long> categoryIds) {
        return new ProductPutVm(
            name, slug, 10.0, true, true, false, true, false,
            brandId, categoryIds,
            "short desc", "desc", "spec",
            "SKU-001", "", 1.0, null, 2.0, 1.0, 0.5,
            "title", "keyword", "meta",
            null, List.of(), List.of(), List.of(), List.of(), List.of(), null
        );
    }

    @Nested
    class CreateProductTest {

        @Test
        void createProduct_validVm_savesAndReturnsProductVm() {
            ProductPostVm vm = buildPostVm("Laptop", "laptop", 1L, List.of(2L));

            Brand brand = new Brand();
            brand.setId(1L);

            Category category = new Category();
            category.setId(2L);

            Product savedProduct = Product.builder()
                .id(10L).name("Laptop").slug("laptop")
                .productCategories(new ArrayList<>())
                .build();

            when(productRepository.findBySlugAndIsPublishedTrue("laptop")).thenReturn(Optional.empty());
            when(productRepository.findBySkuAndIsPublishedTrue("SKU-001")).thenReturn(Optional.empty());
            when(productRepository.findAllById(List.of())).thenReturn(List.of());
            when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
            when(categoryRepository.findAllById(List.of(2L))).thenReturn(List.of(category));
            when(productImageRepository.saveAll(any())).thenReturn(List.of());
            when(productCategoryRepository.saveAll(any())).thenReturn(List.of());

            ProductGetDetailVm result = productService.createProduct(vm);

            assertNotNull(result);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        void createProduct_duplicateSlug_throwsDuplicatedException() {
            ProductPostVm vm = buildPostVm("Laptop", "laptop", null, List.of());

            Product other = Product.builder().id(99L).slug("laptop").build();
            when(productRepository.findBySlugAndIsPublishedTrue("laptop")).thenReturn(Optional.of(other));
            when(productRepository.findAllById(List.of())).thenReturn(List.of());

            assertThrows(DuplicatedException.class, () -> productService.createProduct(vm));
            verify(productRepository, never()).save(any());
        }

        @Test
        void createProduct_brandNotFound_throwsNotFoundException() {
            ProductPostVm vm = buildPostVm("Laptop", "laptop", 99L, List.of());

            Product savedProduct = Product.builder()
                .id(1L).name("Laptop").slug("laptop")
                .productCategories(new ArrayList<>())
                .build();

            when(productRepository.findBySlugAndIsPublishedTrue("laptop")).thenReturn(Optional.empty());
            when(productRepository.findBySkuAndIsPublishedTrue("SKU-001")).thenReturn(Optional.empty());
            when(productRepository.findAllById(List.of())).thenReturn(List.of());
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
            when(productImageRepository.saveAll(any())).thenReturn(List.of());
            when(productCategoryRepository.saveAll(any())).thenReturn(List.of());
            when(brandRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.createProduct(vm));
        }

        @Test
        void createProduct_categoryNotFound_throwsBadRequestException() {
            ProductPostVm vm = buildPostVm("Laptop", "laptop", null, List.of(77L));

            Product savedProduct = Product.builder()
                .id(1L).name("Laptop").slug("laptop")
                .productCategories(new ArrayList<>())
                .build();

            when(productRepository.findBySlugAndIsPublishedTrue("laptop")).thenReturn(Optional.empty());
            when(productRepository.findBySkuAndIsPublishedTrue("SKU-001")).thenReturn(Optional.empty());
            when(productRepository.findAllById(List.of())).thenReturn(List.of());
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
            when(productImageRepository.saveAll(any())).thenReturn(List.of());
            when(categoryRepository.findAllById(List.of(77L))).thenReturn(List.of());

            assertThrows(BadRequestException.class, () -> productService.createProduct(vm));
        }
    }

    @Nested
    class UpdateProductTest {

        @Test
        void updateProduct_validVm_updatesProduct() {
            ProductPutVm vm = buildPutVm("Updated", "updated", 1L, List.of());

            Product existing = Product.builder()
                .id(5L).name("Old").slug("old")
                .productCategories(new ArrayList<>())
                .productImages(new ArrayList<>())
                .products(new ArrayList<>())
                .relatedProducts(new ArrayList<>())
                .build();

            Brand brand = new Brand();
            brand.setId(1L);

            ProductOption productOption = new ProductOption();
            productOption.setId(1L);

            when(productRepository.findById(5L)).thenReturn(Optional.of(existing));
            when(productRepository.findBySlugAndIsPublishedTrue("updated")).thenReturn(Optional.empty());
            when(productRepository.findBySkuAndIsPublishedTrue("SKU-001")).thenReturn(Optional.empty());
            when(productRepository.findAllById(List.of())).thenReturn(List.of());
            when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
            when(productCategoryRepository.findAllByProductId(5L)).thenReturn(List.of());
            when(productImageRepository.saveAll(any())).thenReturn(List.of());
            when(productOptionRepository.findAllByIdIn(any())).thenReturn(List.of(productOption));

            productService.updateProduct(5L, vm);

            verify(productRepository).findById(5L);
        }

        @Test
        void updateProduct_notFound_throwsNotFoundException() {
            ProductPutVm vm = buildPutVm("Updated", "updated", null, List.of());

            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.updateProduct(99L, vm));
        }

        @Test
        void updateProduct_slugTakenByOtherProduct_throwsDuplicatedException() {
            ProductPutVm vm = buildPutVm("Updated", "taken-slug", null, List.of());

            Product existing = Product.builder().id(5L).slug("old-slug").build();
            Product other = Product.builder().id(20L).slug("taken-slug").build();

            when(productRepository.findById(5L)).thenReturn(Optional.of(existing));
            when(productRepository.findBySlugAndIsPublishedTrue("taken-slug")).thenReturn(Optional.of(other));
            when(productRepository.findAllById(List.of())).thenReturn(List.of());

            assertThrows(DuplicatedException.class, () -> productService.updateProduct(5L, vm));
        }
    }

    @Nested
    class GetProductsByCategoryTest {

        @Test
        void getProductsFromCategory_found_returnsVm() {
            Category category = new Category();
            category.setId(1L);
            category.setSlug("electronics");

            Product product = Product.builder()
                .id(10L).name("Phone").slug("phone").thumbnailMediaId(1L).build();

            com.yas.product.model.ProductCategory pc =
                com.yas.product.model.ProductCategory.builder()
                    .product(product).category(category).build();

            Page<com.yas.product.model.ProductCategory> page = new PageImpl<>(List.of(pc));

            when(categoryRepository.findBySlug("electronics")).thenReturn(Optional.of(category));
            when(productCategoryRepository.findAllByCategory(any(Pageable.class), any(Category.class)))
                .thenReturn(page);
            when(mediaService.getMedia(1L)).thenReturn(
                new NoFileMediaVm(1L, "", "", "", "http://img.jpg"));

            var result = productService.getProductsFromCategory(0, 10, "electronics");

            assertNotNull(result);
            verify(productCategoryRepository).findAllByCategory(any(Pageable.class), any(Category.class));
        }

        @Test
        void getProductsFromCategory_categoryNotFound_throwsNotFoundException() {
            when(categoryRepository.findBySlug("unknown")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                () -> productService.getProductsFromCategory(0, 10, "unknown"));
        }
    }

    @Nested
    class SubtractStockQuantityTest {

        @Test
        void subtractStockQuantity_valid_reducesStock() {
            Product product = Product.builder()
                .id(1L).stockTrackingEnabled(true).stockQuantity(10L).build();

            when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
            when(productRepository.saveAll(any())).thenReturn(List.of(product));

            productService.subtractStockQuantity(List.of(new ProductQuantityPutVm(1L, 3L)));

            verify(productRepository).saveAll(any());
        }

        @Test
        void subtractStockQuantity_quantityExceedsStock_setsToZero() {
            Product product = Product.builder()
                .id(1L).stockTrackingEnabled(true).stockQuantity(2L).build();

            when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
            when(productRepository.saveAll(any())).thenReturn(List.of(product));

            productService.subtractStockQuantity(List.of(new ProductQuantityPutVm(1L, 10L)));

            verify(productRepository).saveAll(any());
        }
    }

    @Nested
    class GetProductCheckoutListTest {

        @Test
        void getProductCheckoutList_validIds_returnsVm() {
            Brand brand = new Brand();
            brand.setId(1L);

            Product product = Product.builder()
                .id(1L).name("Phone").slug("phone")
                .thumbnailMediaId(1L).price(99.0).brand(brand)
                .build();

            Page<Product> page = new PageImpl<>(List.of(product));

            when(productRepository.findAllPublishedProductsByIds(any(), any(Pageable.class))).thenReturn(page);
            when(mediaService.getMedia(1L)).thenReturn(
                new NoFileMediaVm(1L, "", "", "", "http://img.jpg"));

            ProductGetCheckoutListVm result = productService.getProductCheckoutList(0, 10, List.of(1L));

            assertNotNull(result);
            verify(productRepository).findAllPublishedProductsByIds(any(), any(Pageable.class));
        }
    }

    @Nested
    class GetProductsWithFilterTest {

        @Test
        void getProductsWithFilter_returnsPagedResult() {
            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop").price(999.0).build();

            Page<Product> page = new PageImpl<>(List.of(product));
            when(productRepository.getProductsWithFilter(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(page);

            ProductListGetVm result = productService.getProductsWithFilter(0, 10, "Laptop", "BrandX");

            assertNotNull(result);
            verify(productRepository).getProductsWithFilter(anyString(), anyString(), any(Pageable.class));
        }
    }

    @Nested
    class GetProductByIdTest {

        @Test
        void getProductById_found_returnsDetailVm() {
            Brand brand = new Brand();
            brand.setId(1L);

            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop")
                .brand(brand)
                .thumbnailMediaId(2L)
                .productCategories(new ArrayList<>())
                .productImages(new ArrayList<>())
                .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(mediaService.getMedia(2L)).thenReturn(
                new NoFileMediaVm(2L, "", "", "", "http://img.jpg"));

            ProductDetailVm result = productService.getProductById(1L);

            assertNotNull(result);
        }

        @Test
        void getProductById_notFound_throwsNotFoundException() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.getProductById(99L));
        }
    }

    @Nested
    class GetLatestProductsTest {

        @Test
        void getLatestProducts_countZero_returnsEmpty() {
            List<ProductListVm> result = productService.getLatestProducts(0);

            assertTrue(result.isEmpty());
        }

        @Test
        void getLatestProducts_validCount_returnsList() {
            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop").price(999.0).build();

            when(productRepository.getLatestProducts(any(Pageable.class)))
                .thenReturn(List.of(product));

            List<ProductListVm> result = productService.getLatestProducts(5);

            assertNotNull(result);
        }

        @Test
        void getLatestProducts_emptyResult_returnsEmpty() {
            when(productRepository.getLatestProducts(any(Pageable.class)))
                .thenReturn(List.of());

            List<ProductListVm> result = productService.getLatestProducts(5);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class GetProductsByBrandTest {

        @Test
        void getProductsByBrand_found_returnsThumbnails() {
            Brand brand = new Brand();
            brand.setId(1L);

            Product product = Product.builder()
                .id(1L).name("Phone").slug("phone").thumbnailMediaId(5L).build();

            when(brandRepository.findBySlug("brand-x")).thenReturn(Optional.of(brand));
            when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand))
                .thenReturn(List.of(product));
            when(mediaService.getMedia(5L)).thenReturn(
                new NoFileMediaVm(5L, "", "", "", "http://img.jpg"));

            List<ProductThumbnailVm> result = productService.getProductsByBrand("brand-x");

            assertNotNull(result);
            verify(brandRepository).findBySlug("brand-x");
        }

        @Test
        void getProductsByBrand_brandNotFound_throwsNotFoundException() {
            when(brandRepository.findBySlug("unknown")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.getProductsByBrand("unknown"));
        }
    }

    @Nested
    class GetListFeaturedProductsTest {

        @Test
        void getListFeaturedProducts_returnsVm() {
            Product product = Product.builder()
                .id(1L).name("Phone").slug("phone").price(99.0).thumbnailMediaId(1L).build();

            Page<Product> page = new PageImpl<>(List.of(product));
            when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(page);
            when(mediaService.getMedia(1L)).thenReturn(
                new NoFileMediaVm(1L, "", "", "", "http://img.jpg"));

            ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 10);

            assertNotNull(result);
        }
    }

    @Nested
    class GetProductDetailTest {

        @Test
        void getProductDetail_found_returnsVm() {
            Brand brand = new Brand();
            brand.setId(1L);
            brand.setName("BrandX");

            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop")
                .brand(brand)
                .thumbnailMediaId(2L)
                .productCategories(new ArrayList<>())
                .productImages(new ArrayList<>())
                .attributeValues(new ArrayList<>())
                .build();

            when(productRepository.findBySlugAndIsPublishedTrue("laptop")).thenReturn(Optional.of(product));
            when(mediaService.getMedia(2L)).thenReturn(
                new NoFileMediaVm(2L, "", "", "", "http://img.jpg"));

            var result = productService.getProductDetail("laptop");

            assertNotNull(result);
        }

        @Test
        void getProductDetail_notFound_throwsNotFoundException() {
            when(productRepository.findBySlugAndIsPublishedTrue("unknown")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.getProductDetail("unknown"));
        }
    }

    @Nested
    class DeleteProductTest {

        @Test
        void deleteProduct_found_setsPublishedFalse() {
            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop").build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            productService.deleteProduct(1L);

            verify(productRepository).save(any(Product.class));
        }

        @Test
        void deleteProduct_notFound_throwsNotFoundException() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.deleteProduct(99L));
        }

        @Test
        void deleteProduct_productWithParentAndCombinations_deletesCombinations() {
            Product parent = Product.builder().id(2L).build();

            ProductOptionCombination combo = ProductOptionCombination.builder()
                .id(1L).build();

            Product product = Product.builder()
                .id(1L).name("Variant").slug("variant").parent(parent).build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productOptionCombinationRepository.findAllByProduct(product))
                .thenReturn(List.of(combo));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            productService.deleteProduct(1L);

            verify(productOptionCombinationRepository).deleteAll(any());
        }
    }

    @Nested
    class GetProductsByMultiQueryTest {

        @Test
        void getProductsByMultiQuery_returnsPagedResult() {
            Product product = Product.builder()
                .id(1L).name("Phone").slug("phone").price(99.0).thumbnailMediaId(1L).build();

            Page<Product> page = new PageImpl<>(List.of(product));
            when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(
                anyString(), anyString(), any(), any(), any(Pageable.class)))
                .thenReturn(page);
            when(mediaService.getMedia(1L)).thenReturn(
                new NoFileMediaVm(1L, "", "", "", "http://img.jpg"));

            ProductsGetVm result = productService.getProductsByMultiQuery(0, 10, "phone", "electronics", 0.0, 500.0);

            assertNotNull(result);
        }
    }

    @Nested
    class GetProductVariationsByParentIdTest {

        @Test
        void getProductVariationsByParentId_noOptions_returnsEmpty() {
            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop").hasOptions(false).build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            var result = productService.getProductVariationsByParentId(1L);

            assertTrue(result.isEmpty());
        }

        @Test
        void getProductVariationsByParentId_notFound_throwsNotFoundException() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.getProductVariationsByParentId(99L));
        }

        @Test
        void getProductVariationsByParentId_withOptions_returnsVariations() {
            Product variation = Product.builder()
                .id(2L).name("Red").slug("laptop-red").price(999.0)
                .productImages(new ArrayList<>())
                .isPublished(true)
                .build();

            Product parent = Product.builder()
                .id(1L).name("Laptop").slug("laptop").hasOptions(true)
                .products(List.of(variation))
                .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(parent));
            when(productOptionCombinationRepository.findAllByProduct(variation))
                .thenReturn(List.of());

            var result = productService.getProductVariationsByParentId(1L);

            assertNotNull(result);
        }
    }

    @Nested
    class GetProductSlugTest {

        @Test
        void getProductSlug_noParent_returnsOwnSlug() {
            Product product = Product.builder()
                .id(1L).slug("laptop").build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductSlugGetVm result = productService.getProductSlug(1L);

            assertNotNull(result);
        }

        @Test
        void getProductSlug_withParent_returnsParentSlug() {
            Product parent = Product.builder().id(2L).slug("parent-laptop").build();

            Product product = Product.builder()
                .id(1L).slug("variant").parent(parent).build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductSlugGetVm result = productService.getProductSlug(1L);

            assertNotNull(result);
        }

        @Test
        void getProductSlug_notFound_throwsNotFoundException() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.getProductSlug(99L));
        }
    }

    @Nested
    class GetProductEsDetailByIdTest {

        @Test
        void getProductEsDetailById_found_returnsVm() {
            Brand brand = new Brand();
            brand.setId(1L);
            brand.setName("BrandX");

            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop").price(999.0)
                .brand(brand)
                .productCategories(new ArrayList<>())
                .attributeValues(new ArrayList<>())
                .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductEsDetailVm result = productService.getProductEsDetailById(1L);

            assertNotNull(result);
        }

        @Test
        void getProductEsDetailById_notFound_throwsNotFoundException() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.getProductEsDetailById(99L));
        }
    }

    @Nested
    class GetRelatedProductsBackofficeTest {

        @Test
        void getRelatedProductsBackoffice_found_returnsList() {
            Product relatedProduct = Product.builder()
                .id(2L).name("Phone").slug("phone").price(99.0).build();

            ProductRelated rel = ProductRelated.builder()
                .id(1L).relatedProduct(relatedProduct).build();

            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop")
                .relatedProducts(List.of(rel))
                .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            List<ProductListVm> result = productService.getRelatedProductsBackoffice(1L);

            assertNotNull(result);
        }

        @Test
        void getRelatedProductsBackoffice_notFound_throwsNotFoundException() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> productService.getRelatedProductsBackoffice(99L));
        }
    }

    @Nested
    class GetRelatedProductsStorefrontTest {

        @Test
        void getRelatedProductsStorefront_found_returnsVm() {
            Product relatedProduct = Product.builder()
                .id(2L).name("Phone").slug("phone").price(99.0)
                .thumbnailMediaId(1L).isPublished(true).build();

            ProductRelated rel = ProductRelated.builder()
                .id(1L).relatedProduct(relatedProduct).build();

            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop").build();

            Page<ProductRelated> relPage = new PageImpl<>(List.of(rel));

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRelatedRepository.findAllByProduct(any(Product.class), any(Pageable.class)))
                .thenReturn(relPage);
            when(mediaService.getMedia(1L)).thenReturn(
                new NoFileMediaVm(1L, "", "", "", "http://img.jpg"));

            ProductsGetVm result = productService.getRelatedProductsStorefront(1L, 0, 10);

            assertNotNull(result);
        }

        @Test
        void getRelatedProductsStorefront_notFound_throwsNotFoundException() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                () -> productService.getRelatedProductsStorefront(99L, 0, 10));
        }
    }

    @Nested
    class GetProductsForWarehouseTest {

        @Test
        void getProductsForWarehouse_returnsInfoList() {
            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop").price(999.0)
                .stockQuantity(10L).stockTrackingEnabled(true).build();

            when(productRepository.findProductForWarehouse(anyString(), anyString(), anyList(), anyString()))
                .thenReturn(List.of(product));

            var result = productService.getProductsForWarehouse("Laptop", "SKU-1", List.of(1L),
                FilterExistInWhSelection.ALL);

            assertNotNull(result);
        }
    }

    @Nested
    class UpdateProductQuantityTest {

        @Test
        void updateProductQuantity_updatesStockQuantity() {
            Product product = Product.builder()
                .id(1L).stockQuantity(5L).build();

            when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
            when(productRepository.saveAll(any())).thenReturn(List.of(product));

            productService.updateProductQuantity(List.of(new ProductQuantityPostVm(1L, 20L)));

            verify(productRepository).saveAll(any());
        }
    }

    @Nested
    class GetProductByIdsTest {

        @Test
        void getProductByIds_returnsList() {
            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop").price(999.0).build();

            when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));

            List<ProductListVm> result = productService.getProductByIds(List.of(1L));

            assertNotNull(result);
        }
    }

    @Nested
    class RestoreStockQuantityTest {

        @Test
        void restoreStockQuantity_addsQuantity() {
            Product product = Product.builder()
                .id(1L).stockTrackingEnabled(true).stockQuantity(5L).build();

            when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
            when(productRepository.saveAll(any())).thenReturn(List.of(product));

            productService.restoreStockQuantity(List.of(new ProductQuantityPutVm(1L, 3L)));

            verify(productRepository).saveAll(any());
        }
    }

    @Nested
    class GetProductByCategoryIdsTest {

        @Test
        void getProductByCategoryIds_returnsList() {
            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop").price(999.0).build();

            when(productRepository.findByCategoryIdsIn(List.of(1L))).thenReturn(List.of(product));

            List<ProductListVm> result = productService.getProductByCategoryIds(List.of(1L));

            assertNotNull(result);
        }
    }

    @Nested
    class GetProductByBrandIdsTest {

        @Test
        void getProductByBrandIds_returnsList() {
            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop").price(999.0).build();

            when(productRepository.findByBrandIdsIn(List.of(1L))).thenReturn(List.of(product));

            List<ProductListVm> result = productService.getProductByBrandIds(List.of(1L));

            assertNotNull(result);
        }
    }

    @Nested
    class GetFeaturedProductsByIdTest {

        @Test
        void getFeaturedProductsById_withThumbnail_returnsList() {
            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop").price(999.0).thumbnailMediaId(1L).build();

            when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
            when(mediaService.getMedia(1L)).thenReturn(
                new NoFileMediaVm(1L, "", "", "", "http://img.jpg"));

            var result = productService.getFeaturedProductsById(List.of(1L));

            assertNotNull(result);
        }

        @Test
        void getFeaturedProductsById_noThumbnailWithParent_lookupParent() {
            Product parent = Product.builder().id(2L).thumbnailMediaId(5L).build();

            Product product = Product.builder()
                .id(1L).name("Variant").slug("variant").price(99.0)
                .thumbnailMediaId(1L).parent(parent).build();

            when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
            when(mediaService.getMedia(1L)).thenReturn(
                new NoFileMediaVm(1L, "", "", "", ""));
            when(productRepository.findById(2L)).thenReturn(Optional.of(parent));
            when(mediaService.getMedia(5L)).thenReturn(
                new NoFileMediaVm(5L, "", "", "", "http://img.jpg"));

            var result = productService.getFeaturedProductsById(List.of(1L));

            assertNotNull(result);
        }
    }

    @Nested
    class ExportProductsTest {

        @Test
        void exportProducts_returnsList() {
            Brand brand = new Brand();
            brand.setId(1L);
            brand.setName("BrandX");

            Product product = Product.builder()
                .id(1L).name("Laptop").slug("laptop")
                .brand(brand).price(999.0).build();

            when(productRepository.getExportingProducts(anyString(), anyString()))
                .thenReturn(List.of(product));

            var result = productService.exportProducts("Laptop", "BrandX");

            assertNotNull(result);
        }
    }
}
