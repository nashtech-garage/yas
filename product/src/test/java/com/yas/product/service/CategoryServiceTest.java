package com.yas.product.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.category.CategoryGetDetailVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryListGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;
    @Mock
    MediaService mediaService;

    @InjectMocks
    CategoryService categoryService;

    Category category;
    CategoryPostVm categoryPostVm;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Laptop");
        category.setSlug("laptop");
        category.setImageId(1L);
        category.setDisplayOrder((short) 1);

        categoryPostVm = org.mockito.Mockito.mock(CategoryPostVm.class);
        org.mockito.Mockito.lenient().when(categoryPostVm.name()).thenReturn("Laptop");
        org.mockito.Mockito.lenient().when(categoryPostVm.slug()).thenReturn("laptop");
        org.mockito.Mockito.lenient().when(categoryPostVm.parentId()).thenReturn(2L);
        org.mockito.Mockito.lenient().when(categoryPostVm.isPublish()).thenReturn(true);
    }

    @Test
    void testGetPageableCategories() {
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(category)));
        CategoryListGetVm result = categoryService.getPageableCategories(0, 10);
        assertEquals(1, result.categoryContent().size());
    }

    @Test
    void testCreate_Success() {
        when(categoryRepository.findExistedName(anyString(), any())).thenReturn(null);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));
        when(categoryRepository.save(any())).thenReturn(category);

        Category result = categoryService.create(categoryPostVm);
        assertEquals(category.getName(), result.getName());
    }

    @Test
    void testCreate_DuplicateName_ThrowsException() {
        when(categoryRepository.findExistedName(anyString(), any())).thenReturn(new Category());
        assertThrows(DuplicatedException.class, () -> categoryService.create(categoryPostVm));
    }

    @Test
    void testUpdate_Success() {
        when(categoryRepository.findExistedName(anyString(), any())).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category)); 
        
        Category parent = new Category();
        parent.setId(2L);
        when(categoryRepository.findById(categoryPostVm.parentId())).thenReturn(Optional.of(parent)); 

        categoryService.update(categoryPostVm, 1L);
        assertEquals(parent, category.getParent());
    }

    @Test
    void testUpdate_CircularParent_ThrowsException() {
        when(categoryRepository.findExistedName(anyString(), any())).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        
        // Cố tình ép repository trả về chính cái danh mục số 1 để giả lập vòng lặp đệ quy
        when(categoryRepository.findById(categoryPostVm.parentId())).thenReturn(Optional.of(category));

        assertThrows(BadRequestException.class, () -> categoryService.update(categoryPostVm, 1L));
    }

    @Test
    void testGetCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        NoFileMediaVm mockMedia = mock(NoFileMediaVm.class);
        when(mockMedia.url()).thenReturn("url");
        when(mediaService.getMedia(anyLong())).thenReturn(mockMedia);

        CategoryGetDetailVm result = categoryService.getCategoryById(1L);
        assertEquals("Laptop", result.name());
    }

    @Test
    void testGetCategories() {
        when(categoryRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(List.of(category));
        NoFileMediaVm mockMedia = mock(NoFileMediaVm.class);
        when(mockMedia.url()).thenReturn("url");
        when(mediaService.getMedia(anyLong())).thenReturn(mockMedia);

        List<CategoryGetVm> result = categoryService.getCategories("Lap");
        assertEquals(1, result.size());
    }

    @Test
    void testGetCategoryByIds() {
        when(categoryRepository.findAllById(any())).thenReturn(List.of(category));
        assertEquals(1, categoryService.getCategoryByIds(List.of(1L)).size());
    }

    @Test
    void testGetTopNthCategories() {
        when(categoryRepository.findCategoriesOrderedByProductCount(any())).thenReturn(List.of("Laptop"));
        assertEquals(1, categoryService.getTopNthCategories(5).size());
    }
}