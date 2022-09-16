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
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.ProductPutVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class ProductServiceTest {

    private ProductRepository productRepository;
    private BrandRepository brandRepository;
    private CategoryRepository categoryRepository;
    private ProductCategoryRepository productCategoryRepository;
    private MediaService mediaService;
    private ProductService productService;
    private Authentication authentication;


    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        brandRepository = mock(BrandRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        productCategoryRepository = mock(ProductCategoryRepository.class);
        mediaService = mock(MediaService.class);
        productService = new ProductService(productRepository, mediaService, brandRepository, productCategoryRepository, categoryRepository);

        //Security config
        authentication = mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("Name");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
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
}