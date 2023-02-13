package com.yas.product.service;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class ProductServiceTest {

    ProductRepository productRepository;
    MediaService mediaService;
    BrandRepository brandRepository;
    CategoryRepository categoryRepository;
    ProductCategoryRepository productCategoryRepository;
    ProductService productService;
    ProductImageRepository productImageRepository;

    ProductPostVm productPostVm;
    List<Category> categoryList;
    Category category1;
    Category category2;
    List<Product> products;
    List<MultipartFile> files;
    MockMultipartFile thumbnail;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        mediaService = mock(MediaService.class);
        brandRepository = mock(BrandRepository.class);
        productRepository = mock(ProductRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        productCategoryRepository = mock(ProductCategoryRepository.class);
        productImageRepository = mock(ProductImageRepository.class);
        productService = new ProductService(
                productRepository,
                mediaService,
                brandRepository,
                productCategoryRepository,
                categoryRepository, productImageRepository);

        productPostVm = new ProductPostVm(
                "name",
                "slug",
                1L,
                List.of(1L, 2L),
                "Short description",
                "description",
                "specification",
                "sku",
                "gtin",
                123.5,
                true,
                true,
                true,
                true,
                "title",
                "meta keyword",
                "meta description",
                1L
        );

        category1 = new Category(1L, null, null, "null", null, null, null, null, null, null);
        category2 = new Category(2L, null, null, "null", null, null, null, null, null, null);
        categoryList = List.of(category1, category2);
        products = List.of(
                new Product(1L, "product1", null, null, null, null, null, "slug", 1.5, false, true, true, false, true, null, null,null,
                        1L, null, null, null, null, null, null ),
                new Product(2L, "product2", null, null, null, null, null, "slug", 1.5, false, true, true, false, true, null, null,null,
                        1L, null, null, null, null, null, null)
        );

        files = List.of(new MockMultipartFile("image.jpg", "image".getBytes()));
        //        Product product = new Product()
        //Security config
        authentication = mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("Name");
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }
    @Test
    void createProduct_TheRequestIsValid_Success() {
        //given
        var productCaptor = ArgumentCaptor.forClass(Product.class);
        Brand brand = mock(Brand.class);
        var productCategoryListCaptor = ArgumentCaptor.forClass(List.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        String username = "admin";
        NoFileMediaVm noFileMediaVm = mock(NoFileMediaVm.class);
        Product parentProduct = new Product(1L, "product1", null, null, null, null, null, "slug", 1.5, false, true, true, false, true, null, null,null,
                        1L, null, null, null, null, null, null );

        when(brandRepository.findById(productPostVm.brandId())).thenReturn(Optional.of(brand));
        when(categoryRepository.findAllById(productPostVm.categoryIds())).thenReturn(categoryList);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(username);
        when(mediaService.saveFile(files.get(0), "", "")).thenReturn(noFileMediaVm);
        Product savedProduct = mock(Product.class);
        when(productRepository.saveAndFlush(productCaptor.capture())).thenReturn(savedProduct);
        Mockito.when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", ""));
        when(productRepository.findById(productPostVm.parentId())).thenReturn(Optional.of(parentProduct));

        //when
        ProductGetDetailVm actualResponse = productService.createProduct(productPostVm, files);

        //then
        verify(productRepository).saveAndFlush(productCaptor.capture());
        Product productValue = productCaptor.getValue();
        assertThat(productValue.getBrand()).isEqualTo(brand);
        assertThat(productValue.getName()).isEqualTo(productPostVm.name());
        assertThat(productValue.getSlug()).isEqualTo(productPostVm.slug());
        assertThat(productValue.getDescription()).isEqualTo(productPostVm.description());
        assertThat(productValue.getShortDescription()).isEqualTo(productPostVm.shortDescription());
        assertThat(productValue.getSpecification()).isEqualTo(productPostVm.specification());
        assertThat(productValue.getSku()).isEqualTo(productPostVm.sku());
        assertThat(productValue.getGtin()).isEqualTo(productPostVm.gtin());
        assertThat(productValue.getMetaKeyword()).isEqualTo(productPostVm.metaKeyword());
        assertThat(productValue.getMetaDescription()).isEqualTo(productPostVm.metaDescription());
        assertThat(productValue.getCreatedBy()).isEqualTo(username);
        assertThat(productValue.getLastModifiedBy()).isEqualTo(username);
        assertThat(productValue.getThumbnailMediaId()).isEqualTo(noFileMediaVm.id());
        List<ProductCategory> productCategoryListValue = productValue.getProductCategories();
        assertThat(productCategoryListValue).hasSize(2);
        assertThat(productCategoryListValue.get(0).getCategory()).isEqualTo(category1);
        assertThat(productCategoryListValue.get(1).getCategory()).isEqualTo(category2);
        assertThat(productCategoryListValue.get(0).getProduct()).isEqualTo(productValue);
        assertThat(productCategoryListValue.get(1).getProduct()).isEqualTo(productValue);

    }

    @Test
    void createProduct_TheRequestContainsNonExistCategoryIdInCategoryList_ThrowsBadRequestException() {
        //given
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category(1L, null, null, "null", null, null, null, null, null, null));

        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(1L);
        categoryIds.add(2L);
        categoryIds.add(3L);

        Brand brand = mock(Brand.class);
        ProductPostVm productPostVm = new ProductPostVm(
                "name",
                "slug",
                1L,
                categoryIds,
                "Short description",
                "description",
                "specification",
                "sku",
                "gtin",
                123.5,
                true,
                true,
                true,
                true,
                "neta title",
                "meta keyword",
                "meta desciption",
                1L
        );

        when(brandRepository.findById(productPostVm.brandId())).thenReturn(Optional.of(brand));
        when(categoryRepository.findAllById(productPostVm.categoryIds())).thenReturn(categoryList);
        List<Long> categoryIdsNotFound = productPostVm.categoryIds();

        //when
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> productService.createProduct(productPostVm, files));

        //then
        assertThat(exception.getMessage()).isEqualTo(String.format("Not found categories %s", categoryIdsNotFound));
    }

    @DisplayName("Create product throws Bad Request Exception when brand id is non exist- negative case")
    @Test
    void createProduct_BrandIdIsNonExist_ThrowsBadRequestException() {
        //given
        Brand brand = mock(Brand.class);
        List<Category> emptyCategoryList = Collections.emptyList();

        when(brandRepository.findById(productPostVm.brandId())).thenReturn(Optional.of(brand));
        when(categoryRepository.findAllById(productPostVm.categoryIds())).thenReturn(emptyCategoryList);

        //when
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> productService.createProduct(productPostVm, files));

        //then
        assertThat(exception.getMessage()).isEqualTo(String.format("Not found categories %s", productPostVm.categoryIds()));
    }


    @DisplayName("Create product throws Not Found Exception when brand id is null- negative case")
    @Test
    void createProduct_BrandIdIsNull_ThrowNotFoundException() {
        //given
        when(brandRepository.findById(productPostVm.brandId())).thenReturn(Optional.empty());

        //when
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> productService.createProduct(productPostVm, files));

        //then
        assertThat(exception.getMessage()).isEqualTo(String.format("Brand %s is not found", productPostVm.brandId()));
    }

    @DisplayName("Get product feature success then return list ProductThumbnailVm")
    @Test
    void getFeaturedProducts_WhenEverythingIsOkay_Success() {
        //given
        List<Product> productList = List.of(
                new Product(1L, "product1", null, null, null, null, null, "slug", 1.5, false, true, true, false, true, null, null,null,
                        1L, null, null, null, null, null, null ),
                new Product(2L, "product2", null, null, null, null, null, "slug", 1.5, false, true, true, false, true, null, null,null,
                        1L, null, null, null, null, null, null)
        );
        String url = "sample-url";
        int totalPage = 20;
        int pageNo = 0;
        int pageSize = 10;
        var pageCaptor  = ArgumentCaptor.forClass(Pageable.class);
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
        when(productRepository.findAllByBrand(existingBrand)).thenReturn(products);
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
    void updateProduct_whenProductIdInvalid_shouldThrowException() {
        //Initial variables
        Long id = Long.valueOf(1);

        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.ofNullable(null));

        //Test
        NotFoundException notFoundException = Assertions.assertThrows(NotFoundException.class, () -> {
                    productService.updateProduct(id, null);
                }
        );
        //Assert
        assertThat(notFoundException.getMessage(), is(String.format("Product %s is not found", id)));
    }

    @Test
    void updateProduct_whenSlugIsDulicated_shouldThrowException() {
        //Initial variables
        Long id = Long.valueOf(1);
        ProductPutVm productPutVm = new ProductPutVm("Test", "Test", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(new Product()));
        Mockito.when(productRepository.findBySlug("Test")).thenReturn(Optional.of(new Product()));

        //Test
        BadRequestException badRequestException = Assertions.assertThrows(BadRequestException.class, () -> {
                    productService.updateProduct(id, productPutVm);
                }
        );
        //Assert
        assertThat(badRequestException.getMessage(), is(String.format("Slug %s is duplicated", productPutVm.slug())));
    }

    @Test
    void updateProduct_whenBrandIdInvalid_shouldThrowException() {
        //Initial variables
        Long id = Long.valueOf(1);
        ProductPutVm productPutVm = new ProductPutVm("Test", "Test", null, null, null, null, id, null, null, null, null, null, null, null, null, null, null);
        Product product = mock(Product.class);
        Brand brand = new Brand();
        brand.setId(id+1);
        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findBySlug("Test")).thenReturn(Optional.ofNullable(null));
        Mockito.when(brandRepository.findById(id)).thenReturn(Optional.ofNullable(null));
        Mockito.when(product.getBrand()).thenReturn(brand);

        //Test
        NotFoundException notFoundException = Assertions.assertThrows(NotFoundException.class, () -> {
                    productService.updateProduct(id, productPutVm);
                }
        );
        //Assert
        assertThat(notFoundException.getMessage(), is(String.format("Brand %s is not found", productPutVm.brandId())));
    }

    @Test
    void updateProduct_whenCategoryIdsInvalid_shouldThrowException() {
        //Initial variables
        Long id = Long.valueOf(1);
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(1L);
        ProductPutVm productPutVm = new ProductPutVm("Test", "Test", null, null, null, null, id, categoryIds, null, null, null, null, null, null, null, null, null);
        Product product = mock(Product.class);
        Brand brand = new Brand();
        brand.setId(id+1);
        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findBySlug("Test")).thenReturn(Optional.ofNullable(null));
        Mockito.when(brandRepository.findById(id)).thenReturn(Optional.of(new Brand()));
        Mockito.when(categoryRepository.findAllById(productPutVm.categoryIds())).thenReturn(new ArrayList<>());
        Mockito.when(product.getBrand()).thenReturn(brand);

        //Test
        BadRequestException badRequestException = Assertions.assertThrows(BadRequestException.class, () -> {
                    productService.updateProduct(id, productPutVm);
                }
        );
        //Assert
        assertThat(badRequestException.getMessage(), is(String.format("Not found categories %s", productPutVm.categoryIds())));
    }

    @Test
    void updateProduct_whenCategoryIdsNotFoundInvalid_shouldThrowException() {
        //Initial variables
        Long id = Long.valueOf(1);
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(1L);
        categoryIds.add(2L);
        List<Category> categoryList = new ArrayList<>();
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setSlug("category-1");
        category.setDescription("Description 1");

        categoryList.add(category);
        ProductPutVm productPutVm = new ProductPutVm("Test", "Test", null, null, null, null, id, categoryIds, null, null, null, null, null, null, null, null, null);

        Product product = mock(Product.class);
        Brand brand = new Brand();
        brand.setId(id+1);
        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findBySlug("Test")).thenReturn(Optional.ofNullable(null));
        Mockito.when(brandRepository.findById(id)).thenReturn(Optional.of(new Brand()));
        Mockito.when(categoryRepository.findAllById(productPutVm.categoryIds())).thenReturn(categoryList);
        Mockito.when(product.getBrand()).thenReturn(brand);

        //Test
        BadRequestException badRequestException = Assertions.assertThrows(BadRequestException.class, () -> {
                    productService.updateProduct(id, productPutVm);
                }
        );
        //Assert
        assertThat(badRequestException.getMessage(), is(String.format("Not found categories %s", categoryIds)));
    }

    @Test
    void updateProduct_whenParamsValid_shouldSuccess() {
        //Initial variables
        Long id = Long.valueOf(1);
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(1L);
        List<Category> categoryList = new ArrayList<>();
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setSlug("category-1");
        category.setDescription("Description 1");
        categoryList.add(category);

        Product product = mock(Product.class);
        Brand brand = new Brand();
        brand.setId(id+1);

        List<ProductCategory> productCategories = new ArrayList<>();
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(category);
        productCategory.setProduct(product);
        productCategories.add(productCategory);


        ProductPutVm productPutVm = new ProductPutVm("Test", "Test", null, null, null, null, id, categoryIds, null, null, null, null, null, null, null, null, null);
        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findBySlug("Test")).thenReturn(Optional.ofNullable(null));
        Mockito.when(brandRepository.findById(id)).thenReturn(Optional.of(new Brand()));
        Mockito.when(categoryRepository.findAllById(productPutVm.categoryIds())).thenReturn(categoryList);
        Mockito.when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", ""));

        Mockito.when(productRepository.saveAndFlush(product)).thenReturn(product);
        Mockito.when(productCategoryRepository.saveAllAndFlush(productCategories)).thenReturn(productCategories);
        Mockito.when(product.getName()).thenReturn("Test");
        Mockito.when(product.getBrand()).thenReturn(brand);

        //Assert
        assertThat(productPutVm.name(), is(productService.updateProduct(id, productPutVm).name()));
    }

    @Test
    void getProduct_whenProductIdInvalid_shouldThrowException() {
        //Initial variables
        Long id = Long.valueOf(1);

        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.ofNullable(null));

        //Test
        NotFoundException notFoundException = Assertions.assertThrows(NotFoundException.class, () -> {
                    productService.updateProduct(id, null);
                }
        );
        //Assert
        assertThat(notFoundException.getMessage(), is(String.format("Product %s is not found", id)));
    }

    @Test
    void getProduct_whenProductIdValid_shouldSuccess() {
        //Initial variables
        Long id = Long.valueOf(1);
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
    void getFeaturedProductsById_whenProductIdValid_shouldSuccess() {
        // Initial variables
        Long id = Long.valueOf(1);
        Product product = mock(Product.class);

        // Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", ""));
        Mockito.when(product.getName()).thenReturn("name");

        assertThat(product.getName(), is(productService.getFeaturedProductsById(id).name()));
    }

    @Test
    void getFeaturedProductsById_whenProductIdInvalid_shouldThrowException() {
        // Initial variables
        Long id = Long.valueOf(1);

        // Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.ofNullable(null));

        // Test
        NotFoundException notFoundException = Assertions.assertThrows(NotFoundException.class, () -> {
            productService.updateProduct(id, null);
        });
        // Assert
        assertThat(notFoundException.getMessage(), is(String.format("Product %s is not found", id)));
    }

    @Test
    void getProductsWithFilter_WhenFilterByBrandNameAndProductName_ThenSuccess() {
        //given
        Page<Product> productPage = mock(Page.class);
        List<ProductListVm> productListVmList = List.of(
                new ProductListVm(products.get(0).getId(), products.get(0).getName(), products.get(0).getSlug()),
                new ProductListVm(products.get(1).getId(), products.get(1).getName(), products.get(1).getSlug())
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
        ProductListGetVm actualReponse = productService.getProductsWithFilter(pageNo, pageSize, productName, brandName);

        //then
        verify(productRepository).getProductsWithFilter(
                productNameCaptor.capture(),
                brandNameCaptor.capture(),
                pageableCaptor.capture());
        assertThat(productNameCaptor.getValue()).contains(productName.trim().toLowerCase());
        assertThat(brandNameCaptor.getValue()).isEqualTo(brandName.trim());
        assertThat(pageableCaptor.getValue()).isEqualTo(PageRequest.of(pageNo, pageSize));

        assertThat(actualReponse.productContent()).isEqualTo(productListVmList);
        assertThat(actualReponse.pageNo()).isEqualTo(productPage.getNumber());
        assertThat(actualReponse.pageSize()).isEqualTo(productPage.getSize());
        assertThat(actualReponse.totalElements()).isEqualTo(productPage.getTotalElements());
        assertThat(actualReponse.totalPages()).isEqualTo(productPage.getTotalPages());
        assertThat(actualReponse.isLast()).isEqualTo(productPage.isLast());
    }

    @Test
    void getProductsWithFilter_WhenFilterByBrandName_ThenSuccess() {
        //given
        Page<Product> productPage = mock(Page.class);
        List<ProductListVm> productListVmList = List.of(
                new ProductListVm(products.get(0).getId(), products.get(0).getName(), products.get(0).getSlug()),
                new ProductListVm(products.get(1).getId(), products.get(1).getName(), products.get(1).getSlug())
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
        ProductListGetVm actualReponse = productService.getProductsWithFilter(pageNo, pageSize, productName, brandName);

        //then
        assertThat(brandNameCaptor.getValue()).isEqualTo(brandName.trim());
        assertThat(pageableCaptor.getValue()).isEqualTo(PageRequest.of(pageNo, pageSize));
        assertThat(actualReponse.productContent()).isEqualTo(productListVmList);
        assertThat(actualReponse.pageNo()).isEqualTo(productPage.getNumber());
        assertThat(actualReponse.pageSize()).isEqualTo(productPage.getSize());
        assertThat(actualReponse.totalElements()).isEqualTo(productPage.getTotalElements());
        assertThat(actualReponse.totalPages()).isEqualTo(productPage.getTotalPages());
        assertThat(actualReponse.isLast()).isEqualTo(productPage.isLast());
    }

    @Test
    void getProductsWithFilter_WhenFilterByProductName_ThenSuccess() {
        //given
        Page<Product> productPage = mock(Page.class);
        List<ProductListVm> productListVmList = List.of(
                new ProductListVm(products.get(0).getId(), products.get(0).getName(), products.get(0).getSlug()),
                new ProductListVm(products.get(1).getId(), products.get(1).getName(), products.get(1).getSlug())
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
        ProductListGetVm actualReponse = productService.getProductsWithFilter(pageNo, pageSize, productName, brandName);

        //then
        assertThat(productNameCaptor.getValue()).isEqualTo(productName.trim().toLowerCase());
        assertThat(pageableCaptor.getValue()).isEqualTo(PageRequest.of(pageNo, pageSize));
        assertThat(actualReponse.productContent()).isEqualTo(productListVmList);
        assertThat(actualReponse.pageNo()).isEqualTo(productPage.getNumber());
        assertThat(actualReponse.pageSize()).isEqualTo(productPage.getSize());
        assertThat(actualReponse.totalElements()).isEqualTo(productPage.getTotalElements());
        assertThat(actualReponse.totalPages()).isEqualTo(productPage.getTotalPages());
        assertThat(actualReponse.isLast()).isEqualTo(productPage.isLast());
    }


    @Test
    void getProductsWithFilter_WhenFindAll_ThenSuccess() {
        //given
        Page<Product> productPage = mock(Page.class);
        List<ProductListVm> productListVmList = List.of(
                new ProductListVm(products.get(0).getId(), products.get(0).getName(), products.get(0).getSlug()),
                new ProductListVm(products.get(1).getId(), products.get(1).getName(), products.get(1).getSlug())
        );
        int pageNo = 1;
        int pageSize = 10;
        int totalElement = 20;
        int totalPages = 4;
        String productName = "";
        String brandName = "";
        var pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(productRepository.findAll(pageableCaptor.capture())).thenReturn(productPage);
        when(productPage.getContent()).thenReturn(products);
        when(productPage.getNumber()).thenReturn(pageNo);
        when(productPage.getTotalElements()).thenReturn((long) totalElement);
        when(productPage.getTotalPages()).thenReturn(totalPages);
        when(productPage.isLast()).thenReturn(false);

        //when
        ProductListGetVm actualReponse = productService.getProductsWithFilter(pageNo, pageSize, productName, brandName);

        //then
        assertThat(pageableCaptor.getValue()).isEqualTo(PageRequest.of(pageNo, pageSize));
        assertThat(actualReponse.productContent()).isEqualTo(productListVmList);
        assertThat(actualReponse.pageNo()).isEqualTo(productPage.getNumber());
        assertThat(actualReponse.pageSize()).isEqualTo(productPage.getSize());
        assertThat(actualReponse.totalElements()).isEqualTo(productPage.getTotalElements());
        assertThat(actualReponse.totalPages()).isEqualTo(productPage.getTotalPages());
        assertThat(actualReponse.isLast()).isEqualTo(productPage.isLast());
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
        when(productCategoryRepository.findAllByCategory(pageableCaptor.capture(),eq(existingCategory))).thenReturn(productCategoryPage);

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
        NoFileMediaVm noFileMediaVm = mock(NoFileMediaVm.class);
        int pageNo = 1;
        int pageSize = 10;
        String productName = "";
        //when
        NotFoundException exception = assertThrows(NotFoundException.class, () ->productService.getProductsFromCategory(pageNo, pageSize, categorySlug));
        //then
        assertThat(exception.getMessage()).isEqualTo(String.format("Category %s is not found", categorySlug));
    }
}