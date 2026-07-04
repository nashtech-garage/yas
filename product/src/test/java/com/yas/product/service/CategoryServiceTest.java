package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.category.CategoryGetDetailVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryListGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private MediaService mediaService;
    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private NoFileMediaVm noFileMediaVm;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("name");
        category.setSlug("slug");
        category.setDescription("description");
        category.setMetaKeyword("metaKeyword");
        category.setMetaDescription("metaDescription");
        category.setDisplayOrder((short) 1);
        category.setIsPublished(true);
        category.setImageId(1L);

        noFileMediaVm = new NoFileMediaVm(1L, "caption", "fileName", "mediaType", "url");
    }

    @Test
    void getCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(mediaService.getMedia(1L)).thenReturn(noFileMediaVm);

        CategoryGetDetailVm categoryGetDetailVm = categoryService.getCategoryById(1L);

        assertNotNull(categoryGetDetailVm);
        assertEquals("name", categoryGetDetailVm.name());
    }

    @Test
    void getCategories_Success() {
        when(categoryRepository.findByNameContainingIgnoreCase("name")).thenReturn(List.of(category));
        when(mediaService.getMedia(anyLong())).thenReturn(noFileMediaVm);

        List<CategoryGetVm> categories = categoryService.getCategories("name");

        assertEquals(1, categories.size());
        assertEquals("name", categories.getFirst().name());
    }

    @Test
    void getCategoriesPageable_Success() {
        when(categoryRepository.findAll(PageRequest.of(0, 1)))
                .thenReturn(new PageImpl<>(List.of(category), PageRequest.of(0, 1), 1));

        CategoryListGetVm response = categoryService.getPageableCategories(0, 1);

        assertEquals(1, response.categoryContent().size());
        assertEquals("name", response.categoryContent().getFirst().name());
    }

    @Test
    void create_Success() {
        CategoryPostVm postVm = new CategoryPostVm("new-cat", "new-slug", "desc", null, "meta", "metaDesc", (short) 1, true, 1L);
        when(categoryRepository.findExistedName("new-cat", null)).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.create(postVm);

        assertNotNull(result);
        assertEquals("new-cat", result.getName());
        assertEquals("new-slug", result.getSlug());
    }

    @Test
    void create_whenDuplicateName_throwsDuplicatedException() {
        CategoryPostVm postVm = new CategoryPostVm("name", "new-slug", "desc", null, "meta", "metaDesc", (short) 1, true, 1L);
        when(categoryRepository.findExistedName("name", null)).thenReturn(category);

        assertThrows(DuplicatedException.class, () -> categoryService.create(postVm));
    }

    @Test
    void update_Success() {
        CategoryPostVm postVm = new CategoryPostVm("updated-cat", "updated-slug", "desc", null, "meta", "metaDesc", (short) 1, true, 1L);
        when(categoryRepository.findExistedName("updated-cat", 1L)).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.update(postVm, 1L);

        assertEquals("updated-cat", category.getName());
        assertEquals("updated-slug", category.getSlug());
        assertNull(category.getParent());
    }

    @Test
    void update_whenParentIsItself_throwsBadRequestException() {
        CategoryPostVm postVm = new CategoryPostVm("updated-cat", "updated-slug", "desc", 1L, "meta", "metaDesc", (short) 1, true, 1L);
        when(categoryRepository.findExistedName("updated-cat", 1L)).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(BadRequestException.class, () -> categoryService.update(postVm, 1L));
    }

    @Test
    void getCategoryByIds_Success() {
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));

        assertEquals(1, categoryService.getCategoryByIds(List.of(1L)).size());
    }

    @Test
    void getTopNthCategories_Success() {
        when(categoryRepository.findCategoriesOrderedByProductCount(PageRequest.of(0, 10))).thenReturn(List.of("name"));

        List<String> categories = categoryService.getTopNthCategories(10);

        assertEquals(List.of("name"), categories);
        verify(categoryRepository).findCategoriesOrderedByProductCount(PageRequest.of(0, 10));
    }
}
