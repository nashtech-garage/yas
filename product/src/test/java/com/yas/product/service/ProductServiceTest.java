package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.ProductRelated;
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
import com.yas.product.viewmodel.product.ProductCheckoutListVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductGetCheckoutListVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductThumbnailGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductVariationGetVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private ProductCategoryRepository productCategoryRepository;
    @Mock
    private CategoryRepository categoryRepository;
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

    @Test
    void getProductById_whenProductExists_thenMapsDetailWithMediaAndRelations() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        Brand brand = brand(10L, "Acme", "acme");
        Category category = category(20L, "Electronics", "electronics");
        product.setBrand(brand);
        product.setProductCategories(List.of(ProductCategory.builder().product(product).category(category).build()));
        product.setProductImages(List.of(ProductImage.builder().imageId(101L).product(product).build()));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(mediaService.getMedia(100L)).willReturn(media(100L, "/thumb.png"));
        given(mediaService.getMedia(101L)).willReturn(media(101L, "/image.png"));

        ProductDetailVm result = productService.getProductById(1L);

        assertEquals(1L, result.id());
        assertEquals(10L, result.brandId());
        assertEquals("/thumb.png", result.thumbnailMedia().url());
        assertEquals(1, result.productImageMedias().size());
        assertEquals("Electronics", result.categories().getFirst().getName());
    }

    @Test
    void getProductsWithFilter_whenRepositoryReturnsPage_thenMapsPagination() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        given(productRepository.getProductsWithFilter(eq("lap"), eq("Acme"), any(Pageable.class)))
            .willReturn(new PageImpl<>(List.of(product)));

        ProductListGetVm result = productService.getProductsWithFilter(0, 10, " Lap ", "Acme");

        assertEquals(1, result.productContent().size());
        assertEquals("Laptop", result.productContent().getFirst().name());
        assertEquals(0, result.pageNo());
    }

    @Test
    void getLatestProducts_whenCountIsPositive_thenMapsProducts() {
        given(productRepository.getLatestProducts(any(Pageable.class)))
            .willReturn(List.of(baseProduct(1L, "Laptop", "laptop")));

        List<ProductListVm> result = productService.getLatestProducts(5);

        assertEquals(1, result.size());
        assertEquals("laptop", result.getFirst().slug());
    }

    @Test
    void getLatestProducts_whenCountIsZero_thenReturnsEmptyList() {
        assertTrue(productService.getLatestProducts(0).isEmpty());
    }

    @Test
    void getProductsByBrand_whenBrandExists_thenMapsThumbnails() {
        Brand brand = brand(10L, "Acme", "acme");
        Product product = baseProduct(1L, "Laptop", "laptop");
        given(brandRepository.findBySlug("acme")).willReturn(Optional.of(brand));
        given(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand)).willReturn(List.of(product));
        given(mediaService.getMedia(100L)).willReturn(media(100L, "/thumb.png"));

        List<ProductThumbnailVm> result = productService.getProductsByBrand("acme");

        assertEquals(1, result.size());
        assertEquals("/thumb.png", result.getFirst().thumbnailUrl());
    }

    @Test
    void getProductsFromCategory_whenCategoryExists_thenMapsPage() {
        Category category = category(20L, "Electronics", "electronics");
        Product product = baseProduct(1L, "Laptop", "laptop");
        ProductCategory productCategory = ProductCategory.builder().product(product).category(category).build();
        given(categoryRepository.findBySlug("electronics")).willReturn(Optional.of(category));
        given(productCategoryRepository.findAllByCategory(any(Pageable.class), eq(category)))
            .willReturn(new PageImpl<>(List.of(productCategory)));
        given(mediaService.getMedia(100L)).willReturn(media(100L, "/thumb.png"));

        ProductListGetFromCategoryVm result = productService.getProductsFromCategory(0, 10, "electronics");

        assertEquals(1, result.productContent().size());
        assertEquals("Laptop", result.productContent().getFirst().name());
    }

    @Test
    void getFeaturedProductsById_whenThumbnailExists_thenMapsThumbnail() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        given(productRepository.findAllByIdIn(List.of(1L))).willReturn(List.of(product));
        given(mediaService.getMedia(100L)).willReturn(media(100L, "/thumb.png"));

        List<ProductThumbnailGetVm> result = productService.getFeaturedProductsById(List.of(1L));

        assertEquals(1, result.size());
        assertEquals("/thumb.png", result.getFirst().thumbnailUrl());
    }

    @Test
    void getListFeaturedProducts_whenRepositoryReturnsPage_thenMapsTotalPages() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        given(productRepository.getFeaturedProduct(any(Pageable.class))).willReturn(new PageImpl<>(List.of(product)));
        given(mediaService.getMedia(100L)).willReturn(media(100L, "/thumb.png"));

        ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 10);

        assertEquals(1, result.productList().size());
        assertEquals(1, result.totalPage());
    }

    @Test
    void deleteProduct_whenParentProduct_thenUnpublishesWithoutDeletingCombinations() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        productService.deleteProduct(1L);

        assertFalse(product.isPublished());
        verify(productRepository).save(product);
    }

    @Test
    void deleteProduct_whenVariation_thenDeletesOptionCombinations() {
        Product parent = baseProduct(1L, "Laptop", "laptop");
        Product variation = baseProduct(2L, "Laptop Red", "laptop-red");
        variation.setParent(parent);
        ProductOptionCombination combination = ProductOptionCombination.builder().product(variation).value("Red").build();
        given(productRepository.findById(2L)).willReturn(Optional.of(variation));
        given(productOptionCombinationRepository.findAllByProduct(variation)).willReturn(List.of(combination));

        productService.deleteProduct(2L);

        assertFalse(variation.isPublished());
        verify(productOptionCombinationRepository).deleteAll(List.of(combination));
        verify(productRepository).save(variation);
    }

    @Test
    void getProductVariationsByParentId_whenParentHasOptions_thenMapsOptionsAndImages() {
        Product parent = baseProduct(1L, "Laptop", "laptop");
        Product variation = baseProduct(2L, "Laptop Red", "laptop-red");
        variation.setProductImages(List.of(ProductImage.builder().imageId(102L).product(variation).build()));
        parent.setHasOptions(true);
        parent.setProducts(List.of(variation));
        ProductOption option = new ProductOption();
        option.setId(5L);
        option.setName("Color");
        ProductOptionCombination combination = ProductOptionCombination.builder()
            .product(variation)
            .productOption(option)
            .value("Red")
            .build();
        given(productRepository.findById(1L)).willReturn(Optional.of(parent));
        given(productOptionCombinationRepository.findAllByProduct(variation)).willReturn(List.of(combination));
        given(mediaService.getMedia(100L)).willReturn(media(100L, "/variation-thumb.png"));
        given(mediaService.getMedia(102L)).willReturn(media(102L, "/variation-image.png"));

        List<ProductVariationGetVm> result = productService.getProductVariationsByParentId(1L);

        assertEquals(1, result.size());
        assertEquals("Red", result.getFirst().options().get(5L));
        assertEquals("/variation-thumb.png", result.getFirst().thumbnail().url());
        assertEquals(1, result.getFirst().productImages().size());
    }

    @Test
    void getProductSlug_whenVariation_thenReturnsParentSlugAndVariationId() {
        Product parent = baseProduct(1L, "Laptop", "laptop");
        Product variation = baseProduct(2L, "Laptop Red", "laptop-red");
        variation.setParent(parent);
        given(productRepository.findById(2L)).willReturn(Optional.of(variation));

        var result = productService.getProductSlug(2L);

        assertEquals("laptop", result.slug());
        assertEquals(2L, result.productVariantId());
    }

    @Test
    void getProductEsDetailById_whenProductExists_thenMapsSearchPayload() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        product.setBrand(brand(10L, "Acme", "acme"));
        product.setProductCategories(List.of(ProductCategory.builder()
            .product(product)
            .category(category(20L, "Electronics", "electronics"))
            .build()));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        var result = productService.getProductEsDetailById(1L);

        assertEquals("Laptop", result.name());
        assertEquals("Acme", result.brand());
        assertEquals(List.of("Electronics"), result.categories());
    }

    @Test
    void updateProductQuantity_whenProductsExist_thenSavesUpdatedStock() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        given(productRepository.findAllByIdIn(List.of(1L))).willReturn(List.of(product));

        productService.updateProductQuantity(List.of(new ProductQuantityPostVm(1L, 7L)));

        assertEquals(7L, product.getStockQuantity());
        verify(productRepository).saveAll(List.of(product));
    }

    @Test
    void subtractAndRestoreStockQuantity_whenDuplicateItems_thenMergeQuantities() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        product.setStockTrackingEnabled(true);
        product.setStockQuantity(10L);
        given(productRepository.findAllByIdIn(List.of(1L, 1L))).willReturn(List.of(product));

        productService.subtractStockQuantity(List.of(new ProductQuantityPutVm(1L, 3L), new ProductQuantityPutVm(1L, 4L)));

        assertEquals(3L, product.getStockQuantity());
        verify(productRepository).saveAll(List.of(product));
    }

    @Test
    void restoreStockQuantity_whenStockTrackingEnabled_thenAddsQuantity() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        product.setStockTrackingEnabled(true);
        product.setStockQuantity(10L);
        given(productRepository.findAllByIdIn(List.of(1L))).willReturn(List.of(product));

        productService.restoreStockQuantity(List.of(new ProductQuantityPutVm(1L, 5L)));

        assertEquals(15L, product.getStockQuantity());
        verify(productRepository).saveAll(List.of(product));
    }

    @Test
    void getProductCheckoutList_whenThumbnailExists_thenMapsThumbnailUrl() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        given(productRepository.findAllPublishedProductsByIds(eq(List.of(1L)), any(Pageable.class)))
            .willReturn(new PageImpl<>(List.of(product)));
        given(mediaService.getMedia(100L)).willReturn(media(100L, "/thumb.png"));

        ProductGetCheckoutListVm result = productService.getProductCheckoutList(0, 10, List.of(1L));

        ProductCheckoutListVm item = result.productCheckoutListVms().getFirst();
        assertEquals("Laptop", item.name());
        assertEquals("/thumb.png", item.thumbnailUrl());
    }

    @Test
    void getProductsForWarehouse_whenRepositoryReturnsProducts_thenMapsInfo() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        given(productRepository.findProductForWarehouse("lap", "sku", List.of(1L), FilterExistInWhSelection.ALL.name()))
            .willReturn(List.of(product));

        var result = productService.getProductsForWarehouse("lap", "sku", List.of(1L), FilterExistInWhSelection.ALL);

        assertEquals(1, result.size());
        assertEquals("Laptop", result.getFirst().name());
    }

    @Test
    void getProductByIds_whenRepositoryReturnsProducts_thenMapsList() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        given(productRepository.findAllByIdIn(List.of(1L))).willReturn(List.of(product));

        List<ProductListVm> result = productService.getProductByIds(List.of(1L));

        assertEquals(1, result.size());
        assertEquals("Laptop", result.getFirst().name());
    }

    @Test
    void getProductByCategoryIds_whenRepositoryReturnsProducts_thenMapsList() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        given(productRepository.findByCategoryIdsIn(List.of(20L))).willReturn(List.of(product));

        List<ProductListVm> result = productService.getProductByCategoryIds(List.of(20L));

        assertEquals(1, result.size());
        assertEquals("Laptop", result.getFirst().name());
    }

    @Test
    void getProductByBrandIds_whenRepositoryReturnsProducts_thenMapsList() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        given(productRepository.findByBrandIdsIn(List.of(10L))).willReturn(List.of(product));

        List<ProductListVm> result = productService.getProductByBrandIds(List.of(10L));

        assertEquals(1, result.size());
        assertEquals("Laptop", result.getFirst().name());
    }

    @Test
    void getProductVariationsByParentId_whenParentHasNoOptions_thenReturnsEmptyList() {
        Product parent = baseProduct(1L, "Laptop", "laptop");
        parent.setHasOptions(false);
        given(productRepository.findById(1L)).willReturn(Optional.of(parent));

        assertTrue(productService.getProductVariationsByParentId(1L).isEmpty());
    }

    @Test
    void getProductSlug_whenProductHasNoParent_thenReturnsOwnSlug() {
        Product product = baseProduct(1L, "Laptop", "laptop");
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        var result = productService.getProductSlug(1L);

        assertEquals("laptop", result.slug());
        assertNull(result.productVariantId());
    }

    private static Product baseProduct(Long id, String name, String slug) {
        return Product.builder()
            .id(id)
            .name(name)
            .slug(slug)
            .shortDescription("short")
            .description("description")
            .specification("spec")
            .sku("sku-" + id)
            .gtin("gtin-" + id)
            .price(99.0)
            .isAllowedToOrder(true)
            .isPublished(true)
            .isFeatured(true)
            .isVisibleIndividually(true)
            .stockTrackingEnabled(false)
            .stockQuantity(0L)
            .thumbnailMediaId(100L)
            .taxClassId(1L)
            .brand(brand(10L, "Acme", "acme"))
            .metaTitle("meta title")
            .metaKeyword("meta keyword")
            .metaDescription("meta description")
            .productImages(List.of())
            .productCategories(List.of())
            .attributeValues(List.of())
            .relatedProducts(List.of())
            .products(List.of())
            .build();
    }

    private static Brand brand(Long id, String name, String slug) {
        Brand brand = new Brand();
        brand.setId(id);
        brand.setName(name);
        brand.setSlug(slug);
        brand.setPublished(true);
        return brand;
    }

    private static Category category(Long id, String name, String slug) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setSlug(slug);
        return category;
    }

    private static NoFileMediaVm media(Long id, String url) {
        return new NoFileMediaVm(id, "caption", "file.png", "image/png", url);
    }
}
