package com.yas.product.controller;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.viewmodel.category.CategoryGetDetailVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class CategoryControllerTest {
    CategoryRepository categoryRepository;
    CategoryController categoryController;
    List<Category> categories;
    Category category;
    Principal principal;
    CategoryPostVm categoryPostVm;
    UriComponentsBuilder uriComponentsBuilder;

    @BeforeEach
    void setUp() {
        uriComponentsBuilder = mock(UriComponentsBuilder.class);
        principal = mock(Principal.class);
        categories = new ArrayList<>();
        categoryRepository = mock(CategoryRepository.class);
        categoryController = new CategoryController(categoryRepository);
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
        List<CategoryGetVm> expect = List.of(
                new CategoryGetVm(1L, "hô hô", "ho-ho", -1)
        );
        when(categoryRepository.findAll()).thenReturn(categories);
        ResponseEntity<List<CategoryGetVm>> actual = categoryController.listCategories();
        assertThat(Objects.requireNonNull(actual.getBody()).size()).isEqualTo(expect.size());
        assertThat(actual.getBody().get(0).id()).isEqualTo(expect.get(0).id());
    }

    @Test
    void getCategory_NotFoundCategoryGetDetailVM_ThrowNotFoundException() {
        when(categoryRepository.findById(2L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> categoryController.getCategory(2L));
    }

    @Test
    void getCategory_ValidCategoryGetDetailVM_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        CategoryGetDetailVm expect = CategoryGetDetailVm.fromModel(category);
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
                (short) 0
        );
        var categoryCaptor = ArgumentCaptor.forClass(Category.class);
        Category savedCategory = mock(Category.class);
        when(categoryRepository.saveAndFlush(categoryCaptor.capture())).thenReturn(savedCategory);
        UriComponentsBuilder newUriComponentsBuilder = mock(UriComponentsBuilder.class);
        UriComponents uriComponents = mock(UriComponents.class);
        when(uriComponentsBuilder.replacePath("/categories/{id}")).thenReturn(newUriComponentsBuilder);
        when(newUriComponentsBuilder.buildAndExpand(savedCategory.getId())).thenReturn(uriComponents);
        ResponseEntity<CategoryGetDetailVm> actual = categoryController.createCategory(categoryPostVm
                , uriComponentsBuilder, principal);
        verify(categoryRepository).saveAndFlush(categoryCaptor.capture());
        Category categoryValue = categoryCaptor.getValue();
        assertThat(categoryValue.getName()).isEqualTo(categoryPostVm.name());
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
                (short) 0
        );
        var categoryCaptor = ArgumentCaptor.forClass(Category.class);
        Category savedCategory = mock(Category.class);
        when(categoryRepository.findById(categoryPostVm.parentId())).thenReturn(Optional.of(category));
        when(categoryRepository.saveAndFlush(categoryCaptor.capture())).thenReturn(savedCategory);
        UriComponentsBuilder newUriComponentsBuilder = mock(UriComponentsBuilder.class);
        UriComponents uriComponents = mock(UriComponents.class);
        when(uriComponentsBuilder.replacePath("/categories/{id}")).thenReturn(newUriComponentsBuilder);
        when(newUriComponentsBuilder.buildAndExpand(savedCategory.getId())).thenReturn(uriComponents);
        ResponseEntity<CategoryGetDetailVm> actual = categoryController.createCategory(categoryPostVm
                , uriComponentsBuilder, principal);
        verify(categoryRepository).saveAndFlush(categoryCaptor.capture());
        Category categoryValue = categoryCaptor.getValue();
        assertThat(categoryValue.getName()).isEqualTo(categoryPostVm.name());
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
                (short) 0
        );
        when(categoryRepository.findById(categoryPostVm.parentId())).thenThrow(BadRequestException.class);
        assertThrows(BadRequestException.class, () -> categoryController.createCategory(categoryPostVm
                , uriComponentsBuilder, principal));
    }
}