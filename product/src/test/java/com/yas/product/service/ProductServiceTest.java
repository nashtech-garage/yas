package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import com.yas.product.model.ProductOptionValue;
import com.yas.product.model.ProductRelated;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
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
import com.yas.product.viewmodel.product.ProductOptionValueDisplay;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductVariationPostVm;
import com.yas.product.viewmodel.product.ProductVariationPutVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePutVm;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private MediaService mediaService;
    @Mock private BrandRepository brandRepository;
    @Mock private ProductCategoryRepository productCategoryRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductImageRepository productImageRepository;
    @Mock private ProductOptionRepository productOptionRepository;
    @Mock private ProductOptionValueRepository productOptionValueRepository;
    @Mock private ProductOptionCombinationRepository productOptionCombinationRepository;
    @Mock private ProductRelatedRepository productRelatedRepository;

    @InjectMocks private ProductService productService;

    @Test
    void createProductShouldSaveMainProductWithBrandCategoriesImagesAndRelations() {
        ProductPostVm postVm = productPostVm(List.of(), List.of(), List.of(), List.of(9L));
        Brand brand = brand(1L, "Acme", "acme");
        Category category = category(2L, "Phones", "phones");
        Product relatedProduct = product(9L, "Related", "related");

        when(productRepository.findAllById(List.of())).thenReturn(List.of());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });
        when(categoryRepository.findAllById(List.of(2L))).thenReturn(List.of(category));
        when(productRepository.findAllById(List.of(9L))).thenReturn(List.of(relatedProduct));

        var result = productService.createProduct(postVm);

        assertThat(result.id()).isEqualTo(10L);
        verify(productCategoryRepository).saveAll(anyList());
        verify(productImageRepository).saveAll(anyList());
        verify(productRelatedRepository).saveAll(anyList());
    }

    @Test
    void createProductShouldCreateVariationsOptionsAndCombinations() {
        ProductVariationPostVm variation = new ProductVariationPostVm(
            "Red phone", "PHONE-RED", "SKU-RED", "GTIN-RED", 12.5, 101L, List.of(201L), Map.of(7L, "Red"));
        ProductPostVm postVm = productPostVm(
            List.of(variation),
            List.of(new ProductOptionValuePostVm(7L, "text", 1, List.of("Red"))),
            List.of(new ProductOptionValueDisplay(7L, "text", 1, "Red")),
            List.of());
        ProductOption option = productOption(7L, "Color");

        when(productRepository.findAllById(List.of())).thenReturn(List.of());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand(1L, "Acme", "acme")));
        when(categoryRepository.findAllById(List.of(2L))).thenReturn(List.of(category(2L, "Phones", "phones")));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            if (saved.getId() == null) {
                saved.setId(10L);
            }
            return saved;
        });
        when(productRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Product> products = invocation.getArgument(0);
            products.forEach(product -> product.setId(20L));
            return products;
        });
        when(productOptionRepository.findAllByIdIn(List.of(7L))).thenReturn(List.of(option));
        when(productOptionValueRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = productService.createProduct(postVm);

        assertThat(result.id()).isEqualTo(10L);
        verify(productOptionCombinationRepository).saveAll(anyList());
        verify(productRepository).saveAll(anyList());
    }

    @Test
    void createProductShouldRejectInvalidDimensionsAndDuplicates() {
        ProductPostVm invalidDimensions = productPostVmWithDimensions(3D, 5D);

        assertThatThrownBy(() -> productService.createProduct(invalidDimensions))
            .isInstanceOf(BadRequestException.class);

        ProductPostVm duplicateSlug = productPostVmWithDimensions(5D, 4D);
        Product existing = product(99L, "Existing", "phone");
        when(productRepository.findBySlugAndIsPublishedTrue("phone")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> productService.createProduct(duplicateSlug))
            .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void setProductImagesShouldCreateDeleteOrKeepImages() {
        Product productWithoutImages = product(10L, "Phone", "phone");
        productWithoutImages.setProductImages(null);

        assertThat(productService.setProductImages(List.of(1L, 2L), productWithoutImages))
            .extracting(ProductImage::getImageId)
            .containsExactly(1L, 2L);

        Product productWithImages = product(11L, "Phone", "phone-2");
        productWithImages.setProductImages(List.of(
            ProductImage.builder().imageId(1L).product(productWithImages).build(),
            ProductImage.builder().imageId(2L).product(productWithImages).build()));

        assertThat(productService.setProductImages(List.of(2L, 3L), productWithImages))
            .extracting(ProductImage::getImageId)
            .containsExactly(3L);
        verify(productImageRepository).deleteByImageIdInAndProductId(List.of(1L), 11L);

        assertThat(productService.setProductImages(List.of(), productWithImages)).isEmpty();
        verify(productImageRepository).deleteByProductId(11L);
    }

    @Test
    void readMethodsShouldMapProductPagesAndDetails() {
        Product product = richProduct();
        when(mediaService.getMedia(any())).thenAnswer(invocation ->
            new NoFileMediaVm(invocation.getArgument(0), "caption", "file", "image/png", "url-" + invocation.getArgument(0)));
        when(productRepository.getProductsWithFilter(any(), any(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(product)));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(productRepository.getLatestProducts(any(Pageable.class))).thenReturn(List.of(product));

        assertThat(productService.getProductsWithFilter(0, 10, " Phone ", "Acme").productContent()).hasSize(1);
        assertThat(productService.getProductById(10L).thumbnailMedia().url()).isEqualTo("url-100");
        assertThat(productService.getLatestProducts(0)).isEmpty();
        assertThat(productService.getLatestProducts(2)).hasSize(1);
    }

    @Test
    void storefrontListingMethodsShouldMapMediaAndPagination() {
        Product product = richProduct();
        Brand brand = product.getBrand();
        Category category = product.getProductCategories().getFirst().getCategory();
        when(mediaService.getMedia(any())).thenAnswer(invocation ->
            new NoFileMediaVm(invocation.getArgument(0), "caption", "file", "image/png", "url-" + invocation.getArgument(0)));
        when(brandRepository.findBySlug("acme")).thenReturn(Optional.of(brand));
        when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand)).thenReturn(List.of(product));
        when(categoryRepository.findBySlug("phones")).thenReturn(Optional.of(category));
        when(productCategoryRepository.findAllByCategory(any(Pageable.class), any(Category.class)))
            .thenReturn(new PageImpl<>(product.getProductCategories()));
        when(productRepository.findAllByIdIn(List.of(10L))).thenReturn(List.of(product));
        when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(product)));
        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(any(), any(), any(), any(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(product)));
        when(productRepository.findAllPublishedProductsByIds(anyList(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(product)));

        assertThat(productService.getProductsByBrand("acme")).hasSize(1);
        assertThat(productService.getProductsFromCategory(0, 10, "phones").productContent()).hasSize(1);
        assertThat(productService.getFeaturedProductsById(List.of(10L))).hasSize(1);
        assertThat(productService.getListFeaturedProducts(0, 10).productList()).hasSize(1);
        assertThat(productService.getProductsByMultiQuery(0, 10, "phone", "phones", 1D, 20D).productContent()).hasSize(1);
        assertThat(productService.getProductCheckoutList(0, 10, List.of(10L)).productCheckoutListVms()).hasSize(1);
    }

    @Test
    void detailSearchAndRelationMethodsShouldMapDomainObjects() {
        Product product = richProduct();
        Product related = product(30L, "Case", "case");
        related.setPublished(true);
        related.setThumbnailMediaId(300L);
        product.setRelatedProducts(List.of(ProductRelated.builder().product(product).relatedProduct(related).build()));

        when(mediaService.getMedia(any())).thenAnswer(invocation ->
            new NoFileMediaVm(invocation.getArgument(0), "caption", "file", "image/png", "url-" + invocation.getArgument(0)));
        when(productRepository.findBySlugAndIsPublishedTrue("phone")).thenReturn(Optional.of(product));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(productRelatedRepository.findAllByProduct(any(Product.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(product.getRelatedProducts()));
        when(productRepository.findProductForWarehouse("phone", "sku", List.of(10L), "ALL")).thenReturn(List.of(product));
        when(productRepository.findByCategoryIdsIn(List.of(2L))).thenReturn(List.of(product));
        when(productRepository.findByBrandIdsIn(List.of(1L))).thenReturn(List.of(product));
        when(productRepository.findAllByIdIn(List.of(10L))).thenReturn(List.of(product));

        assertThat(productService.getProductDetail("phone").productImageMediaUrls()).contains("url-101");
        assertThat(productService.getProductEsDetailById(10L).categories()).contains("Phones");
        assertThat(productService.getRelatedProductsBackoffice(10L)).hasSize(1);
        assertThat(productService.getRelatedProductsStorefront(10L, 0, 10).productContent()).hasSize(1);
        assertThat(productService.getProductsForWarehouse("phone", "sku", List.of(10L), FilterExistInWhSelection.ALL)).hasSize(1);
        assertThat(productService.getProductByCategoryIds(List.of(2L))).hasSize(1);
        assertThat(productService.getProductByBrandIds(List.of(1L))).hasSize(1);
        assertThat(productService.getProductByIds(List.of(10L))).hasSize(1);
    }

    @Test
    void variationSlugDeleteAndStockMethodsShouldHandleBranches() {
        Product parent = product(10L, "Phone", "phone");
        parent.setHasOptions(true);
        Product variation = product(20L, "Red", "phone-red");
        variation.setParent(parent);
        variation.setPublished(true);
        variation.setThumbnailMediaId(200L);
        variation.setProductImages(List.of(ProductImage.builder().imageId(201L).product(variation).build()));
        parent.setProducts(List.of(variation));
        ProductOption option = productOption(7L, "Color");
        ProductOptionCombination combination = ProductOptionCombination.builder()
            .product(variation).productOption(option).value("Red").displayOrder(1).build();

        when(mediaService.getMedia(any())).thenAnswer(invocation ->
            new NoFileMediaVm(invocation.getArgument(0), "caption", "file", "image/png", "url-" + invocation.getArgument(0)));
        when(productRepository.findById(10L)).thenReturn(Optional.of(parent));
        when(productRepository.findById(20L)).thenReturn(Optional.of(variation));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of(combination));
        when(productRepository.findAllByIdIn(anyList())).thenAnswer(invocation -> {
            List<Long> ids = invocation.getArgument(0);
            List<Product> products = new ArrayList<>();
            if (ids.contains(10L)) {
                products.add(parent);
            }
            if (ids.contains(20L)) {
                products.add(variation);
            }
            return products;
        });

        assertThat(productService.getProductVariationsByParentId(10L)).hasSize(1);
        assertThat(productService.getProductSlug(20L).slug()).isEqualTo("phone");
        productService.deleteProduct(20L);
        verify(productOptionCombinationRepository).deleteAll(List.of(combination));

        productService.updateProductQuantity(List.of(new ProductQuantityPostVm(10L, 4L), new ProductQuantityPostVm(20L, 6L)));
        assertThat(parent.getStockQuantity()).isEqualTo(4L);
        parent.setStockTrackingEnabled(true);
        parent.setStockQuantity(4L);
        variation.setStockTrackingEnabled(true);
        variation.setStockQuantity(1L);
        productService.subtractStockQuantity(List.of(new ProductQuantityPutVm(10L, 10L), new ProductQuantityPutVm(20L, 2L)));
        assertThat(parent.getStockQuantity()).isZero();
        productService.restoreStockQuantity(List.of(new ProductQuantityPutVm(10L, 3L), new ProductQuantityPutVm(10L, 2L)));
        assertThat(parent.getStockQuantity()).isEqualTo(5L);
    }

    @Test
    void notFoundMethodsShouldThrow() {
        when(productRepository.findById(404L)).thenReturn(Optional.empty());
        when(brandRepository.findBySlug("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(404L)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> productService.getProductsByBrand("missing")).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> productService.deleteProduct(404L)).isInstanceOf(NotFoundException.class);
    }

    private ProductPostVm productPostVm(
        List<ProductVariationPostVm> variations,
        List<ProductOptionValuePostVm> optionValues,
        List<ProductOptionValueDisplay> optionValueDisplays,
        List<Long> relatedProductIds) {
        return new ProductPostVm("Phone", "PHONE", 1L, new ArrayList<>(List.of(2L)), "short", "description",
            "spec", "SKU", "GTIN", 1D, DimensionUnit.CM, 5D, 3D, 1D, 10D,
            true, true, true, true, true, "title", "keyword", "meta", 100L, List.of(101L),
            variations, optionValues, optionValueDisplays, relatedProductIds, 5L);
    }

    private ProductPostVm productPostVmWithDimensions(Double length, Double width) {
        return new ProductPostVm("Phone", "PHONE", 1L, new ArrayList<>(List.of(2L)), "short", "description",
            "spec", "SKU", "GTIN", 1D, DimensionUnit.CM, length, width, 1D, 10D,
            true, true, true, true, true, "title", "keyword", "meta", 100L, List.of(101L),
            List.of(), List.of(), List.of(), List.of(), 5L);
    }

    private ProductPutVm productPutVm(
        List<ProductVariationPutVm> variations,
        List<ProductOptionValuePutVm> optionValues,
        List<ProductOptionValueDisplay> optionValueDisplays,
        List<Long> relatedProductIds) {
        return new ProductPutVm("New Phone", "NEW-PHONE", 20D, true, true, false, true, true, 3L,
            new ArrayList<>(List.of(4L)), "new short", "new description", "new spec", "SKU-N", "GTIN-N",
            2D, DimensionUnit.CM, 5D, 4D, 2D, "new title", "new keyword", "new meta", 103L,
            List.of(104L), variations, optionValues, optionValueDisplays, relatedProductIds, 6L);
    }

    private Product product(Long id, String name, String slug) {
        Product product = Product.builder()
            .id(id)
            .name(name)
            .slug(slug)
            .sku("sku-" + id)
            .gtin("gtin-" + id)
            .shortDescription("short")
            .description("description")
            .specification("spec")
            .price(10D)
            .isAllowedToOrder(true)
            .isPublished(true)
            .isFeatured(false)
            .isVisibleIndividually(true)
            .stockTrackingEnabled(true)
            .stockQuantity(2L)
            .taxClassId(5L)
            .metaTitle("title")
            .metaKeyword("keyword")
            .metaDescription("meta")
            .weight(1D)
            .dimensionUnit(DimensionUnit.CM)
            .length(5D)
            .width(4D)
            .height(3D)
            .productCategories(new ArrayList<>())
            .productImages(new ArrayList<>())
            .attributeValues(new ArrayList<>())
            .relatedProducts(new ArrayList<>())
            .products(new ArrayList<>())
            .build();
        return product;
    }

    private Product richProduct() {
        Product product = product(10L, "Phone", "phone");
        Brand brand = brand(1L, "Acme", "acme");
        Category category = category(2L, "Phones", "phones");
        product.setBrand(brand);
        product.setThumbnailMediaId(100L);
        product.setProductImages(List.of(ProductImage.builder().imageId(101L).product(product).build()));
        product.setProductCategories(List.of(productCategory(product, category)));
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Specs");
        ProductAttribute attribute = ProductAttribute.builder().id(1L).name("Memory").productAttributeGroup(group).build();
        ProductAttributeValue attributeValue = new ProductAttributeValue();
        attributeValue.setProduct(product);
        attributeValue.setProductAttribute(attribute);
        attributeValue.setValue("128GB");
        product.setAttributeValues(List.of(attributeValue));
        return product;
    }

    private Brand brand(Long id, String name, String slug) {
        Brand brand = new Brand();
        brand.setId(id);
        brand.setName(name);
        brand.setSlug(slug);
        return brand;
    }

    private Category category(Long id, String name, String slug) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setSlug(slug);
        return category;
    }

    private ProductCategory productCategory(Product product, Category category) {
        return ProductCategory.builder().product(product).category(category).build();
    }

    private ProductOption productOption(Long id, String name) {
        ProductOption productOption = new ProductOption();
        productOption.setId(id);
        productOption.setName(name);
        return productOption;
    }

}
