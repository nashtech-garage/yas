package com.yas.product.service;

import com.yas.product.exception.NotFoundException;
import com.yas.product.model.*;
import com.yas.product.repository.*;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    ProductRepository productRepository;
    MediaService mediaService;
    BrandRepository brandRepository;
    CategoryRepository categoryRepository;
    ProductCategoryRepository productCategoryRepository;
    ProductService productService;
    ProductImageRepository productImageRepository;
    ProductOptionRepository productOptionRepository;
    ProductOptionValueRepository productOptionValueRepository;
    ProductOptionCombinationRepository productOptionCombinationRepository;
    ProductRelatedRepository productRelatedRepository;

    List<Category> categoryList;
    Category category1;
    Category category2;
    List<Product> products;
    List<MultipartFile> files;

    private final ZonedDateTime CREATED_ON = ZonedDateTime.now();

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        mediaService = mock(MediaService.class);
        brandRepository = mock(BrandRepository.class);
        productRepository = mock(ProductRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        productCategoryRepository = mock(ProductCategoryRepository.class);
        productImageRepository = mock(ProductImageRepository.class);
        productOptionRepository = mock(ProductOptionRepository.class);
        productOptionValueRepository = mock(ProductOptionValueRepository.class);
        productOptionCombinationRepository = mock(ProductOptionCombinationRepository.class);
        productRelatedRepository = mock(ProductRelatedRepository.class);
        productService = new ProductService(
                productRepository,
                mediaService,
                brandRepository,
                productCategoryRepository,
                categoryRepository,
                productImageRepository,
                productOptionRepository,
                productOptionValueRepository,
                productOptionCombinationRepository,
                productRelatedRepository);
        category1 = new Category(1L, "category", null, "category", null, null, null, false, 1L, null, null, null);
        category2 = new Category(2L, "category2", null, "category2", null, null, null, false, 1L, null, null, null);
        categoryList = List.of(category1, category2);
        Product product1 = Product.builder()
                .id(1L)
                .name("product1")
                .slug("slug1")
                .isAllowedToOrder(true)
                .isPublished(true)
                .isFeatured(true)
                .isVisibleIndividually(true)
                .stockTrackingEnabled(true)
                .thumbnailMediaId(1L)
                .productImages(new ArrayList<>() {
                    {
                        add(ProductImage.builder()
                                .id(1L)
                                .imageId(2L)
                                .build());
                    }
                })
                .taxClassId(1L)
                .build();
        product1.setCreatedOn(CREATED_ON);
        Product product2 = Product.builder()
                .id(2L)
                .name("product2")
                .slug("slug2")
                .isAllowedToOrder(true)
                .isPublished(true)
                .isFeatured(true)
                .isVisibleIndividually(true)
                .stockTrackingEnabled(true)
                .thumbnailMediaId(1L)
                .productImages(new ArrayList<>() {
                    {
                        add(ProductImage.builder()
                                .id(2L)
                                .imageId(3L)
                                .build());
                    }
                })
                .taxClassId(1L)
                .build();
        product2.setCreatedOn(CREATED_ON);

        products = List.of(product1, product2);

        files = List.of(new MockMultipartFile("image.jpg", "image".getBytes()));
    }

    @DisplayName("Get product feature success then return list ProductThumbnailVm")
    @Test
    void getFeaturedProducts_WhenEverythingIsOkay_Success() {
        //given
        List<Product> productList = List.of(
                Product.builder()
                        .id(1L)
                        .name("product1")
                        .slug("slug1")
                        .thumbnailMediaId(1L)
                        .sku("sku")
                        .build(),
                Product.builder()
                        .id(2L)
                        .name("product2")
                        .sku("sku")
                        .slug("slug2")
                        .thumbnailMediaId(1L)
                        .build());
        String url = "sample-url";
        int totalPage = 20;
        int pageNo = 0;
        int pageSize = 10;
        var pageCaptor = ArgumentCaptor.forClass(Pageable.class);
        Page<Product> productPage = mock(Page.class);
        NoFileMediaVm noFileMediaVm = mock(NoFileMediaVm.class);

        when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(productPage);
        when(productPage.getContent()).thenReturn(productList);
        when(productPage.getTotalPages()).thenReturn(totalPage);
        when(mediaService.getMedia(anyLong())).thenReturn(noFileMediaVm);
        when(noFileMediaVm.url()).thenReturn(url);

        //when
        ProductFeatureGetVm actualResponse = productService.getListFeaturedProducts(pageNo, pageSize);

        //then
        verify(productRepository).getFeaturedProduct(pageCaptor.capture());
        assertThat(actualResponse.totalPage()).isEqualTo(totalPage);
        assertThat(actualResponse.productList().size()).isEqualTo(2);
        for (int i = 0; i < actualResponse.productList().size(); i++) {
            Product product = products.get(i);
            assertThat(actualResponse.productList().get(i).id()).isEqualTo(product.getId());
            assertThat(actualResponse.productList().get(i).name()).isEqualTo(product.getName());
            assertThat(actualResponse.productList().get(i).slug()).isEqualTo(product.getSlug());
            assertThat(actualResponse.productList().get(i).thumbnailUrl()).isEqualTo(mediaService.getMedia(product.getThumbnailMediaId()).url());
        }
    }


    @DisplayName("Get products by brand when brand is available with slug then success")
    @Test
    void getProductsByBrand_BrandSlugIsValid_Success() {
        //given
        String brandSlug = "iphone";
        String url = "sample-url";
        Brand existingBrand = mock(Brand.class);
        NoFileMediaVm noFileMediaVm = mock(NoFileMediaVm.class);

        when(brandRepository.findBySlug(brandSlug)).thenReturn(Optional.of(existingBrand));
        when(productRepository.findAllByBrandAndIsPublishedTrue(existingBrand)).thenReturn(products);
        when(mediaService.getMedia(anyLong())).thenReturn(noFileMediaVm);
        when(noFileMediaVm.url()).thenReturn(url);

        //when
        List<ProductThumbnailVm> actualResponse = productService.getProductsByBrand(brandSlug);

        //then
        assertThat(actualResponse).hasSize(2);
        for (int i = 0; i < actualResponse.size(); i++) {
            Product product = products.get(i);
            assertThat(actualResponse.get(i).id()).isEqualTo(product.getId());
            assertThat(actualResponse.get(i).name()).isEqualTo(product.getName());
            assertThat(actualResponse.get(i).slug()).isEqualTo(product.getSlug());
            assertThat(actualResponse.get(i).thumbnailUrl()).isEqualTo(mediaService.getMedia(product.getThumbnailMediaId()).url());
        }
    }

    @DisplayName("Get products by brand when brand is non exist then throws exception")
    @Test
    void getProductsByBrand_BrandIsNonExist_ThrowsNotFoundException() {
        //given
        String brandSlug = "iphone";
        when(brandRepository.findBySlug(brandSlug)).thenReturn(Optional.empty());

        //when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.getProductsByBrand(brandSlug));

        //then
        assertThat(exception.getMessage()).isEqualTo(String.format("Brand %s is not found", brandSlug));
    }

    @Test
    void getProduct_whenProductIdInvalid_shouldThrowException() {
        // Initial variables
        Long id = 1L;
        ProductPutVm productPutVm = Mockito.mock(ProductPutVm.class);

        // Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.empty());

        // Test
        NotFoundException notFoundException = Assertions.assertThrows(NotFoundException.class, () ->
                productService.updateProduct(id, productPutVm));

        // Assert
        assertThat(notFoundException.getMessage(), is(String.format("Product %s is not found", id)));
    }

    @Test
    void getProduct_whenProductIdValid_shouldSuccess() {
        //Initial variables
        long id = 1L;
        Product product = mock(Product.class);
        Brand brand = new Brand();
        brand.setId(1L);

        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", ""));
        Mockito.when(product.getName()).thenReturn("name");
        Mockito.when(product.getBrand()).thenReturn(brand);

        assertThat(product.getName(), is(productService.getProductById(id).name()));
    }

    @Test
    void getListFeaturedProductsByListProductIds_whenAllProductIdsValid_shouldSuccess() {
        // Initial variables
        Long[] ids = {1L};
        List<Long> productIds = Arrays.asList(ids);
        Product product = mock(Product.class);

        // Stub
        Mockito.when(productRepository.findAllByIdIn(productIds)).thenReturn(List.of(product));
        Mockito.when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", ""));

        assertThat(1, is(productService.getFeaturedProductsById(productIds).size()));
    }

    @Test
    void getFeaturedProductsById_whenProductIdInvalid_shouldThrowException() {
        // Initial variables
        Long id = 1L;
        ProductPutVm productPutVm = Mockito.mock(ProductPutVm.class);

        // Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.empty());

        // Test
        NotFoundException notFoundException = Assertions.assertThrows(NotFoundException.class, () ->
                productService.updateProduct(id, productPutVm));

        // Assert
        assertThat(notFoundException.getMessage(), is(String.format("Product %s is not found", id)));
    }

    @Test
    void getProductsWithFilter_WhenFilterByBrandNameAndProductName_ThenSuccess() {
        //given
        Page<Product> productPage = mock(Page.class);
        List<ProductListVm> productListVmList = List.of(
                new ProductListVm(products.get(0).getId(), products.get(0).getName(), products.get(0).getSlug(),
                        true, true, true, true, CREATED_ON, products.get(0).getTaxClassId()),
                new ProductListVm(products.get(1).getId(), products.get(1).getName(), products.get(1).getSlug(),
                        true, true, true, true, CREATED_ON, products.get(0).getTaxClassId())
        );
        int pageNo = 1;
        int pageSize = 10;
        int totalElement = 20;
        int totalPages = 4;
        String productName = " Xiaomi ";
        String brandName = " Xiaomi ";
        var pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        var productNameCaptor = ArgumentCaptor.forClass(String.class);
        var brandNameCaptor = ArgumentCaptor.forClass(String.class);

        when(productRepository.getProductsWithFilter(anyString(), anyString(), any(Pageable.class))).thenReturn(productPage);
        when(productPage.getContent()).thenReturn(products);
        when(productPage.getNumber()).thenReturn(pageNo);
        when(productPage.getTotalElements()).thenReturn((long) totalElement);
        when(productPage.getTotalPages()).thenReturn(totalPages);
        when(productPage.isLast()).thenReturn(false);

        //when
        ProductListGetVm actualResponse = productService.getProductsWithFilter(pageNo, pageSize, productName, brandName);

        //then
        verify(productRepository).getProductsWithFilter(
                productNameCaptor.capture(),
                brandNameCaptor.capture(),
                pageableCaptor.capture());
        assertThat(productNameCaptor.getValue()).contains(productName.trim().toLowerCase());
        assertThat(brandNameCaptor.getValue()).isEqualTo(brandName.trim());
        assertThat(pageableCaptor.getValue()).isEqualTo(PageRequest.of(pageNo, pageSize));

        assertThat(actualResponse.productContent()).isEqualTo(productListVmList);
        assertThat(actualResponse.pageNo()).isEqualTo(productPage.getNumber());
        assertThat(actualResponse.pageSize()).isEqualTo(productPage.getSize());
        assertThat(actualResponse.totalElements()).isEqualTo(productPage.getTotalElements());
        assertThat(actualResponse.totalPages()).isEqualTo(productPage.getTotalPages());
        assertThat(actualResponse.isLast()).isEqualTo(productPage.isLast());
    }

    @Test
    void getProductsWithFilter_WhenFilterByBrandName_ThenSuccess() {
        //given
        Page<Product> productPage = mock(Page.class);
        List<ProductListVm> productListVmList = List.of(
                new ProductListVm(products.get(0).getId(), products.get(0).getName(), products.get(0).getSlug(),
                        products.get(0).isAllowedToOrder(), products.get(0).isPublished(),
                        products.get(0).isFeatured(), products.get(0).isVisibleIndividually(), products.get(0).getCreatedOn(), products.get(0).getTaxClassId()),
                new ProductListVm(products.get(1).getId(), products.get(1).getName(), products.get(1).getSlug(),
                        products.get(0).isAllowedToOrder(), products.get(0).isPublished(),
                        products.get(0).isFeatured(), products.get(0).isVisibleIndividually(), products.get(0).getCreatedOn(), products.get(0).getTaxClassId())
        );
        int pageNo = 1;
        int pageSize = 10;
        int totalElement = 20;
        int totalPages = 4;
        String productName = " ";
        String brandName = " Xiaomi ";
        var pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        var brandNameCaptor = ArgumentCaptor.forClass(String.class);
        var productNameCaptor = ArgumentCaptor.forClass(String.class);

        when(productRepository.getProductsWithFilter(productNameCaptor.capture(), brandNameCaptor.capture(), pageableCaptor.capture())).thenReturn(productPage);
        when(productPage.getContent()).thenReturn(products);
        when(productPage.getNumber()).thenReturn(pageNo);
        when(productPage.getTotalElements()).thenReturn((long) totalElement);
        when(productPage.getTotalPages()).thenReturn(totalPages);
        when(productPage.isLast()).thenReturn(false);

        //when
        ProductListGetVm actualResponse = productService.getProductsWithFilter(pageNo, pageSize, productName, brandName);

        //then
        assertThat(brandNameCaptor.getValue()).isEqualTo(brandName.trim());
        assertThat(pageableCaptor.getValue()).isEqualTo(PageRequest.of(pageNo, pageSize));
        assertThat(actualResponse.productContent()).isEqualTo(productListVmList);
        assertThat(actualResponse.pageNo()).isEqualTo(productPage.getNumber());
        assertThat(actualResponse.pageSize()).isEqualTo(productPage.getSize());
        assertThat(actualResponse.totalElements()).isEqualTo(productPage.getTotalElements());
        assertThat(actualResponse.totalPages()).isEqualTo(productPage.getTotalPages());
        assertThat(actualResponse.isLast()).isEqualTo(productPage.isLast());
    }

    @Test
    void getProductsWithFilter_WhenFilterByProductName_ThenSuccess() {
        //given
        Page<Product> productPage = mock(Page.class);
        List<ProductListVm> productListVmList = List.of(
                new ProductListVm(products.get(0).getId(), products.get(0).getName(), products.get(0).getSlug(),
                        products.get(0).isAllowedToOrder(), products.get(0).isPublished(),
                        products.get(0).isFeatured(), products.get(0).isVisibleIndividually(), products.get(0).getCreatedOn(), products.get(0).getTaxClassId()),
                new ProductListVm(products.get(1).getId(), products.get(1).getName(), products.get(1).getSlug(),
                        products.get(0).isAllowedToOrder(), products.get(0).isPublished(),
                        products.get(0).isFeatured(), products.get(0).isVisibleIndividually(), products.get(0).getCreatedOn(), products.get(0).getTaxClassId())
        );
        int pageNo = 1;
        int pageSize = 10;
        int totalElement = 20;
        int totalPages = 4;
        String productName = " Xiaomi 12";
        String brandName = "  ";
        var pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        var productNameCaptor = ArgumentCaptor.forClass(String.class);
        var brandNameCaptor = ArgumentCaptor.forClass(String.class);

        when(productRepository.getProductsWithFilter(productNameCaptor.capture(), brandNameCaptor.capture(), pageableCaptor.capture())).thenReturn(productPage);
        when(productPage.getContent()).thenReturn(products);
        when(productPage.getNumber()).thenReturn(pageNo);
        when(productPage.getTotalElements()).thenReturn((long) totalElement);
        when(productPage.getTotalPages()).thenReturn(totalPages);
        when(productPage.isLast()).thenReturn(false);

        //when
        ProductListGetVm actualResponse = productService.getProductsWithFilter(pageNo, pageSize, productName, brandName);

        //then
        assertThat(productNameCaptor.getValue()).isEqualTo(productName.trim().toLowerCase());
        assertThat(pageableCaptor.getValue()).isEqualTo(PageRequest.of(pageNo, pageSize));
        assertThat(actualResponse.productContent()).isEqualTo(productListVmList);
        assertThat(actualResponse.pageNo()).isEqualTo(productPage.getNumber());
        assertThat(actualResponse.pageSize()).isEqualTo(productPage.getSize());
        assertThat(actualResponse.totalElements()).isEqualTo(productPage.getTotalElements());
        assertThat(actualResponse.totalPages()).isEqualTo(productPage.getTotalPages());
        assertThat(actualResponse.isLast()).isEqualTo(productPage.isLast());
    }

    @Test
    void getProductsWithFilter_whenFindAll_thenSuccess() {
        // Create a mock ProductRepository and mock Page object
        Page<Product> productPage = mock(Page.class);

        // Set up mock behavior for ProductRepository and Page
        when(productRepository.getProductsWithFilter(anyString(), anyString(), any(Pageable.class))).thenReturn(productPage);
        when(productPage.getContent()).thenReturn(products);
        when(productPage.getNumber()).thenReturn(0);
        when(productPage.getSize()).thenReturn(2);
        when(productPage.getTotalElements()).thenReturn(2L);
        when(productPage.getTotalPages()).thenReturn(1);
        when(productPage.isLast()).thenReturn(true);

        // Create an instance of the class under test and call the method
        ProductListGetVm result = productService.getProductsWithFilter(0, 2, "product", "Brand");

        // Verify that the mock objects were called with the expected arguments
        verify(productRepository).getProductsWithFilter("product", "Brand", PageRequest.of(0, 2));

        // Verify that the result has the expected values
        assertEquals(2, result.productContent().size());
        assertEquals(0, result.pageNo());
        assertEquals(2, result.pageSize());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());
        assertTrue(result.isLast());

    }

    @Test
    void getProductsFromCategory_WhenFindAllByCategory_ThenSuccess() {
        //given
        Page<ProductCategory> productCategoryPage = mock(Page.class);
        List<ProductCategory> productCategoryList = List.of(
                new ProductCategory(1L, products.get(0), null, 1, true),
                new ProductCategory(2L, products.get(1), null, 2, true)
        );
        String categorySlug = "laptop-macbook";
        String url = "sample-url";
        var existingCategory = mock(Category.class);
        NoFileMediaVm noFileMediaVm = mock(NoFileMediaVm.class);
        int pageNo = 1;
        int pageSize = 10;
        int totalElement = 20;
        int totalPages = 4;
        var pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(categoryRepository.findBySlug(categorySlug)).thenReturn(Optional.of(existingCategory));
        when(productCategoryRepository.findAllByCategory(pageableCaptor.capture(), eq(existingCategory))).thenReturn(productCategoryPage);

        when(productCategoryPage.getContent()).thenReturn(productCategoryList);
        when(productCategoryPage.getNumber()).thenReturn(pageNo);
        when(productCategoryPage.getTotalElements()).thenReturn((long) totalElement);
        when(productCategoryPage.getTotalPages()).thenReturn(totalPages);
        when(productCategoryPage.isLast()).thenReturn(false);
        when(mediaService.getMedia(anyLong())).thenReturn(noFileMediaVm);
        when(noFileMediaVm.url()).thenReturn(url);

        //when
        ProductListGetFromCategoryVm actualResponse = productService.getProductsFromCategory(pageNo, pageSize, categorySlug);

        //then
        assertThat(actualResponse.productContent()).hasSize(2);
        assertThat(actualResponse.pageNo()).isEqualTo(productCategoryPage.getNumber());
        assertThat(actualResponse.pageSize()).isEqualTo(productCategoryPage.getSize());
        assertThat(actualResponse.totalElements()).isEqualTo(productCategoryPage.getTotalElements());
        assertThat(actualResponse.totalPages()).isEqualTo(productCategoryPage.getTotalPages());
        assertThat(actualResponse.isLast()).isEqualTo(productCategoryPage.isLast());
    }

    @Test
    void getProductsFromCategory_CategoryIsNonExist_ThrowsNotFoundException() {
        //given
        String categorySlug = "laptop-macbook";
        when(categoryRepository.findBySlug(categorySlug)).thenReturn(Optional.empty());
        int pageNo = 1;
        int pageSize = 10;

        //when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.getProductsFromCategory(pageNo, pageSize, categorySlug));

        //then
        assertThat(exception.getMessage()).isEqualTo(String.format("Category %s is not found", categorySlug));
    }

    @Test
    void deleteProduct_givenProductIdValid_thenSuccess() {
        // Initial variables
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Call method under test
        productService.deleteProduct(productId);

        // Verifying that the repository was called with expected parameters
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productRepository).findById(idCaptor.capture());
        Long capturedId = idCaptor.getValue();
        assertEquals(productId, capturedId);

        verify(productRepository).save(product);
        assertFalse(product.isPublished());
    }

    @Test
    void deleteProductAttribute_givenProductAttributeIdInvalid_thenThrowNotFoundException() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> productService.deleteProduct(productId));
    }

    @Test
    void getProductsByMultiQuery_WhenFilterByBrandNameAndProductName_ThenSuccess() {
        // Given
        int pageNo = 0;
        int pageSize = 9;
        Double startPrice = 1.0;
        Double endPrice = 10.0;
        String productName = "product1";
        String categorySlug = "category1";
        String url = "sample-url";

        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 2), 2);
        NoFileMediaVm noFileMediaVm = mock(NoFileMediaVm.class);

        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(
                anyString(), anyString(), anyDouble(), anyDouble(), any(Pageable.class))).thenReturn(productPage);
        when(mediaService.getMedia(anyLong())).thenReturn(new NoFileMediaVm(null, "", "", "", url));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        ArgumentCaptor<String> productNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> categorySlugCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> startPriceCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> endPriceCaptor = ArgumentCaptor.forClass(Double.class);

        // Call method under test
        ProductsGetVm result = productService.getProductsByMultiQuery(pageNo, pageSize, productName, categorySlug, startPrice, endPrice);

        // Verifying that the repository was called with expected parameters
        verify(productRepository).findByProductNameAndCategorySlugAndPriceBetween(
                productNameCaptor.capture(), categorySlugCaptor.capture(), startPriceCaptor.capture(),
                endPriceCaptor.capture(), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue()).isEqualTo(PageRequest.of(pageNo, pageSize));
        assertThat(productNameCaptor.getValue()).contains(productName.trim().toLowerCase());
        assertEquals("product1", productNameCaptor.getValue());
        assertEquals("category1", categorySlugCaptor.getValue());

        // Assert result
        assertEquals(2, result.productContent().size());
        assertEquals(0, result.pageNo());
        assertEquals(2, result.pageSize());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());
        assertTrue(result.isLast());
        assertThat(result.pageNo()).isEqualTo(productPage.getNumber());
        assertThat(result.pageSize()).isEqualTo(productPage.getSize());
        assertThat(result.totalElements()).isEqualTo(productPage.getTotalElements());
        assertThat(result.totalPages()).isEqualTo(productPage.getTotalPages());
        assertThat(result.isLast()).isEqualTo(productPage.isLast());

        when(mediaService.getMedia(anyLong())).thenReturn(noFileMediaVm);
        when(noFileMediaVm.url()).thenReturn(url);
    }
}