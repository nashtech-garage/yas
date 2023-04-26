package com.yas.product.controller;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.service.CategoryService;
import com.yas.product.viewmodel.ImageVm;
import com.yas.product.viewmodel.category.CategoryGetDetailVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CategoryControllerTest {
    CategoryRepository categoryRepository;
    CategoryService categoryService;
    CategoryController categoryController;
    List<Category> categories;
    Category category;
    Principal principal;
    CategoryPostVm categoryPostVm;
    UriComponentsBuilder uriComponentsBuilder;

    @BeforeEach
    void setUp() {
        uriComponentsBuilder = mock(UriComponentsBuilder.class);
        categoryService = mock(CategoryService.class);
        principal = mock(Principal.class);
        categories = new ArrayList<>();
        categoryRepository = mock(CategoryRepository.class);
        categoryController = new CategoryController(categoryRepository, categoryService);
        category = new Category();
        category.setId(1L);
        category.setName("hô hô");
        category.setSlug("ho-ho");
        category.setMetaKeyword("ho ho");
        category.setMetaDescription("ho ho");
        category.setDisplayOrder((short) 0);
        categories.add(category);
        when(principal.getName()).thenReturn("user");
    }

    @Test
    void ListCategories_ValidListCategoryGetVM_Success() {
        ImageVm categoryImage = new ImageVm(1L, "url");
        List<CategoryGetVm> expect = List.of(
                new CategoryGetVm(1L, "hô hô", "ho-ho", -1, categoryImage)
        );
        when(categoryService.getCategories()).thenReturn(expect);
        ResponseEntity<List<CategoryGetVm>> actual = categoryController.listCategories();
        assertThat(Objects.requireNonNull(actual.getBody()).size()).isEqualTo(expect.size());
        assertThat(actual.getBody().get(0).id()).isEqualTo(expect.get(0).id());
    }

    @Test
    void getCategory_NotFoundCategoryGetDetailVM_ThrowNotFoundException() {
        when(categoryService.getCategoryById(2L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> categoryController.getCategory(2L));
    }

    @Test
    void getCategory_ValidCategoryGetDetailVM_Success() {
        CategoryGetDetailVm expect = CategoryGetDetailVm.fromModel(category);
        when(categoryService.getCategoryById(1L)).thenReturn(expect);
        ResponseEntity<CategoryGetDetailVm> actual = categoryController.getCategory(1L);
        assertEquals(Objects.requireNonNull(actual.getBody()).Id(), expect.Id());
    }

    @Test
    void createCategory_ValidCategoryWithOutParentId_Success() {
        categoryPostVm = new CategoryPostVm(
                "name",
                "slug",
                "description",
                null,
                "",
                "",
                (short) 0,
                false,
                1L
        );
        Category savedCategory = mockSavedCategory();
        when(categoryService.create(any())).thenReturn(savedCategory);
        UriComponentsBuilder newUriComponentsBuilder = mock(UriComponentsBuilder.class);
        UriComponents uriComponents = mock(UriComponents.class);
        when(uriComponentsBuilder.replacePath("/categories/{id}")).thenReturn(newUriComponentsBuilder);
        when(newUriComponentsBuilder.buildAndExpand(savedCategory.getId())).thenReturn(uriComponents);
        categoryController.createCategory(categoryPostVm, uriComponentsBuilder, principal);
        assertThat(savedCategory.getName()).isEqualTo(categoryPostVm.name());
    }

    private Category mockSavedCategory() {
        Category savedCategory = new Category();
        savedCategory.setName(categoryPostVm.name());
        savedCategory.setId(1L);
        savedCategory.setDisplayOrder((short) 1);

        return savedCategory;
    }

    @Test
    void createCategory_ValidCategoryWithParentId_Success() {
        categoryPostVm = new CategoryPostVm(
                "name",
                "slug",
                "description",
                1L,
                "",
                "",
                (short) 0,
                false,
                1L
        );
        Category savedCategory = mockSavedCategory();
        when(categoryService.create(any())).thenReturn(savedCategory);
        UriComponentsBuilder newUriComponentsBuilder = mock(UriComponentsBuilder.class);
        UriComponents uriComponents = mock(UriComponents.class);
        when(uriComponentsBuilder.replacePath("/categories/{id}")).thenReturn(newUriComponentsBuilder);
        when(newUriComponentsBuilder.buildAndExpand(savedCategory.getId())).thenReturn(uriComponents);
        categoryController.createCategory(categoryPostVm
                , uriComponentsBuilder, principal);
        assertThat(savedCategory.getName()).isEqualTo(categoryPostVm.name());
    }

    @Test
    void createCategory_ValidCategoryWithNotFoundParentId_ThrowNotFoundException() {
        categoryPostVm = new CategoryPostVm(
                "name",
                "slug",
                "description",
                2L,
                "",
                "",
                (short) 0,
                false,
                1L
        );
        when(categoryService.create(any())).thenThrow(BadRequestException.class);
        assertThrows(BadRequestException.class, () -> categoryController.createCategory(categoryPostVm
                , uriComponentsBuilder, principal));
    }
}