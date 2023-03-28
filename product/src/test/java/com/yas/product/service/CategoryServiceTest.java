package com.yas.product.service;

import com.yas.product.controller.CategoryController;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CategoryServiceTest {
    private CategoryRepository categoryRepository;
    private MediaService mediaService;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        mediaService = mock(MediaService.class);
        categoryService = new CategoryService(categoryRepository, mediaService);
    }

    @Test
    void getCategoryById_Success() {
        Category category = new Category();
        category.setId(1L);
        category.setName("name");
        category.setSlug("slug");
        category.setDescription("description");
        category.setMetaKeyword("metaKeyword");
        category.setMetaDescription("metaDescription");
        category.setDisplayOrder((short) 1);
        category.setIsPublished(true);
        category.setImageId(1L);
        NoFileMediaVm noFileMediaVm = new NoFileMediaVm(1L, "caption", "fileName", "mediaType", "url");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(mediaService.getMedia(category.getImageId())).thenReturn(noFileMediaVm);
        Assertions.assertNotNull(categoryService.getCategoryById(1L));
    }

    @Test
    void getCategories_Success() {
        Category category = new Category();
        category.setId(1L);
        category.setName("name");
        category.setSlug("slug");
        category.setDescription("description");
        category.setMetaKeyword("metaKeyword");
        category.setMetaDescription("metaDescription");
        category.setDisplayOrder((short) 1);
        category.setIsPublished(true);
        category.setImageId(1L);
        List<Category> categoryList = List.of(category);
        NoFileMediaVm noFileMediaVm = new NoFileMediaVm(1L, "caption", "fileName", "mediaType", "url");

        when(categoryRepository.findAll()).thenReturn(categoryList);
        when(mediaService.getMedia(category.getImageId())).thenReturn(noFileMediaVm);
        Assertions.assertEquals(categoryService.getCategories().size(), categoryList.size());
    }
}
