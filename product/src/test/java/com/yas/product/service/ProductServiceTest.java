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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
                "meta keyword",
                "meta desciption"
        );

        category1 = new Category(1L, null, null, "null", null, null, null, null, null, null);
        category2 = new Category(2L, null, null, "null", null, null, null, null, null, null);
        categoryList = List.of(category1, category2);
        products = List.of(
                new Product(1L, "product1", null, null, null, null, null, "slug", 1.5, true, true, false, null, null,
                        1L, null, null, null, null),
                new Product(2L, "product2", null, null, null, null, null, "slug", 1.5, true, true, false, null, null,
                        1L, null, null, null, null)
        );

        files = List.of(new MockMultipartFile("image.jpg", "image".getBytes()));
        //        Product product = new Product()
        //Security config
        authentication = mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("Name");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    
    }


    @Test
    void getProducts_ExistProductsInDatabase_Sucsess() {
        //given
        List<ProductListVm> productListVmExpected = List.of(
                new ProductListVm(1L, "product1", "slug"),
                new ProductListVm(2L, "product2", "slug")
        );
        when(productRepository.findAll()).thenReturn(products);

        //when
        List<ProductListVm> productListVmActual = productService.getProducts();

        //then
        assertThat(productListVmActual).hasSameSizeAs(productListVmExpected);
        assertThat(productListVmActual.get(0)).isEqualTo(productListVmExpected.get(0));
        assertThat(productListVmActual.get(1)).isEqualTo(productListVmExpected.get(1));

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

        when(brandRepository.findById(productPostVm.brandId())).thenReturn(Optional.of(brand));
        when(categoryRepository.findAllById(productPostVm.categoryIds())).thenReturn(categoryList);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(username);
        when(mediaService.SaveFile(files.get(0), "", "")).thenReturn(noFileMediaVm);
        Product savedProduct = mock(Product.class);
        when(productRepository.saveAndFlush(productCaptor.capture())).thenReturn(savedProduct);
        Mockito.when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", ""));

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

        verify(productCategoryRepository).saveAllAndFlush(productCategoryListCaptor.capture());
        List<ProductCategory> productCategoryListValue = productCategoryListCaptor.getValue();
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

        List<Long> categoryIds= new ArrayList<>();
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
                "meta keyword",
                "meta desciption"
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
        String url = "sample-url";
        NoFileMediaVm noFileMediaVm = mock(NoFileMediaVm.class);

        when(productRepository.findAll()).thenReturn(products);
        when(mediaService.getMedia(anyLong())).thenReturn(noFileMediaVm);
        when(noFileMediaVm.url()).thenReturn(url);

        //when
        List<ProductThumbnailVm> actualResponse = productService.getFeaturedProducts();

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


    @DisplayName("Get products by category when exist with the slug then success")
    @Test
    void getProductsByCategory_CategorySlugIsValid_Success() {
        //given
        String categorySlug = "laptop-macbook";
        String url = "sample-url";
        Category existingCategory = mock(Category.class);
        NoFileMediaVm noFileMediaVm = mock(NoFileMediaVm.class);

        List<ProductCategory> productCategoryList = List.of(
                new ProductCategory(1L, products.get(0), null, 1, true),
                new ProductCategory(2L, products.get(1), null, 2, true)
        );

        when(categoryRepository.findBySlug(categorySlug)).thenReturn(Optional.of(existingCategory));
        when(productCategoryRepository.findAllByCategory(existingCategory)).thenReturn(productCategoryList);
        when(mediaService.getMedia(anyLong())).thenReturn(noFileMediaVm);
        when(noFileMediaVm.url()).thenReturn(url);

        //when
        List<ProductThumbnailVm> actualResponse = productService.getProductsByCategory(categorySlug);

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

    @DisplayName("Get products by category when category is non exist then throws exception")
    @Test
    void getProductsByCategory_CategoryIsNonExist_ThrowsNotFoundException() {
        //given
        String categorySlug = "laptop-macbook";
        when(categoryRepository.findBySlug(categorySlug)).thenReturn(Optional.empty());

        //when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.getProductsByCategory(categorySlug));

        //then
        assertThat(exception.getMessage()).isEqualTo(String.format("Category %s is not found", categorySlug));
    }
    @Test
    void updateProduct_whenProductIdInvalid_shouldThrowException() {
        //Initial variables
        Long id = Long.valueOf(1);

        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.ofNullable(null));

        //Test
        NotFoundException notFoundException = Assertions.assertThrows(NotFoundException.class, () -> {
                    productService.updateProduct(id,null);
                }
        );
        //Assert
        assertThat(notFoundException.getMessage(), is(String.format("Product %s is not found", id)));
    }

    @Test
    void updateProduct_whenSlugIsDulicated_shouldThrowException() {
        //Initial variables
        Long id = Long.valueOf(1);
        ProductPutVm productPutVm = new ProductPutVm("Test", "Test", id, null, null, null, null, null, null, null, null, null);

        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(new Product()));
        Mockito.when(productRepository.findBySlug("Test")).thenReturn(Optional.of(new Product()));

        //Test
        BadRequestException badRequestException = Assertions.assertThrows(BadRequestException.class, () -> {
                    productService.updateProduct(id,productPutVm);
                }
        );
        //Assert
        assertThat(badRequestException.getMessage(), is(String.format("Slug %s is duplicated", productPutVm.slug())));
    }
    @Test
    void updateProduct_whenBrandIdInvalid_shouldThrowException() {
        //Initial variables
        Long id = Long.valueOf(1);
        ProductPutVm productPutVm = new ProductPutVm("Test", "Test", id, null, null, null, null, null, null, null, null, null);

        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(new Product()));
        Mockito.when(productRepository.findBySlug("Test")).thenReturn(Optional.ofNullable(null));
        Mockito.when(brandRepository.findById(id)).thenReturn(Optional.ofNullable(null));

        //Test
        NotFoundException notFoundException = Assertions.assertThrows(NotFoundException.class, () -> {
                    productService.updateProduct(id,productPutVm);
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
        ProductPutVm productPutVm = new ProductPutVm("Test", "Test", id, categoryIds, null, null, null, null, null, null, null, null);

        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(new Product()));
        Mockito.when(productRepository.findBySlug("Test")).thenReturn(Optional.ofNullable(null));
        Mockito.when(brandRepository.findById(id)).thenReturn(Optional.of(new Brand()));
        Mockito.when(categoryRepository.findAllById(productPutVm.categoryIds())).thenReturn(new ArrayList<>());

        //Test
        BadRequestException badRequestException = Assertions.assertThrows(BadRequestException.class, () -> {
                    productService.updateProduct(id,productPutVm);
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
        ProductPutVm productPutVm = new ProductPutVm("Test", "Test", id, categoryIds, null, null, null, null, null, null, null, null);

        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(new Product()));
        Mockito.when(productRepository.findBySlug("Test")).thenReturn(Optional.ofNullable(null));
        Mockito.when(brandRepository.findById(id)).thenReturn(Optional.of(new Brand()));
        Mockito.when(categoryRepository.findAllById(productPutVm.categoryIds())).thenReturn(categoryList);
        //Test
        BadRequestException badRequestException = Assertions.assertThrows(BadRequestException.class, () -> {
                    productService.updateProduct(id,productPutVm);
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

        List<ProductCategory> productCategories = new ArrayList<>();
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(category);
        productCategory.setProduct(product);
        productCategories.add(productCategory);


        ProductPutVm productPutVm = new ProductPutVm("Test", "Test", id, categoryIds, null, null, null, null, null, null, null, null);
        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findBySlug("Test")).thenReturn(Optional.ofNullable(null));
        Mockito.when(brandRepository.findById(id)).thenReturn(Optional.of(new Brand()));
        Mockito.when(categoryRepository.findAllById(productPutVm.categoryIds())).thenReturn(categoryList);
        Mockito.when(mediaService.SaveFile(productPutVm.thumbnail(), "", "")).thenReturn(new NoFileMediaVm(1L, "", "", "", ""));
        Mockito.when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", ""));

        Mockito.when(productRepository.saveAndFlush(product)).thenReturn(product);
        Mockito.when(productCategoryRepository.saveAllAndFlush(productCategories)).thenReturn(productCategories);
        Mockito.when(product.getName()).thenReturn("Test");

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
                    productService.updateProduct(id,null);
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

        //Stub
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", ""));
        Mockito.when(product.getName()).thenReturn("name");

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
    void getAllProducts_WhenRequestIsValid_ThenSuccess() {
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
        var pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(productRepository.findByOrderByIdAsc(pageableCaptor.capture())).thenReturn(productPage);
        when(productPage.getContent()).thenReturn(products);
        when(productPage.getNumber()).thenReturn(pageNo);
        when(productPage.getTotalElements()).thenReturn((long) totalElement);
        when(productPage.getTotalPages()).thenReturn(totalPages);
        when(productPage.isLast()).thenReturn(false);

        //when
        ProductListGetVm actualReponse = productService.getAllProducts(pageNo, pageSize);

        //then
        assertThat(pageableCaptor.getValue()).isEqualTo(PageRequest.of(pageNo, pageSize));

        assertThat(actualReponse.productContent()).isEqualTo(productListVmList);
        assertThat(actualReponse.pageNo()).isEqualTo(productPage.getNumber());
        assertThat(actualReponse.pageSize()).isEqualTo(productPage.getSize());
        assertThat(actualReponse.totalElements()).isEqualTo(productPage.getTotalElements());
        assertThat(actualReponse.totalPages()).isEqualTo(productPage.getTotalPages());
        assertThat(actualReponse.isLast()).isEqualTo(productPage.isLast());
    }
}