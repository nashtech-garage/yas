package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.product.ProductApplication;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.category.CategoryGetDetailVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;
@SpringBootTest(classes = ProductApplication.class)
class CategoryServiceTest {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @MockBean
    private MediaService mediaService;
    @Autowired
    private CategoryService categoryService;
    private Category category;
    private NoFileMediaVm noFileMediaVm;

    @BeforeEach
    void setUp() {

        category = new Category();
        category.setName("name");
        category.setSlug("slug");
        category.setDescription("description");
        category.setMetaKeyword("metaKeyword");
        category.setMetaDescription("metaDescription");
        category.setDisplayOrder((short) 1);
        category.setIsPublished(true);
        category.setImageId(1L);
        categoryRepository.save(category);

        noFileMediaVm = new NoFileMediaVm(1L, "caption", "fileName", "mediaType", "url");
    }

    @AfterEach
    void tearDown() {
        productCategoryRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void getCategoryById_Success() {
        when(mediaService.getMedia(category.getImageId())).thenReturn(noFileMediaVm);
        CategoryGetDetailVm categoryGetDetailVm = categoryService.getCategoryById(category.getId());
        assertNotNull(categoryGetDetailVm);
        assertEquals("name", categoryGetDetailVm.name());
    }

    @Test
    void getCategories_Success() {
        when(mediaService.getMedia(any())).thenReturn(noFileMediaVm);
        Assertions.assertEquals(1, categoryService.getCategories("name").size());
        CategoryGetVm categoryGetVm = categoryService.getCategories("name").getFirst();
        assertEquals("name", categoryGetVm.name());
    }

    @Test
    void getCategoriesPageable_Success() {
        when(mediaService.getMedia(category.getImageId())).thenReturn(noFileMediaVm);
        Assertions.assertEquals(1, categoryService.getPageableCategories(0, 1).categoryContent().size());
        CategoryGetVm categoryGetVm = categoryService.getCategories("a").getFirst();
        assertEquals("name", categoryGetVm.name());
    }

@Test
    void getCategoryById_WhenCategoryNotFound_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(9999L));
    }

    @Test
    void create_WhenValidData_ShouldCreateSuccessfully() {
        // Lưu ý thứ tự tham số: name, slug, description, parentId, metaKeywords, metaDescription, displayOrder, isPublish, imageId
        CategoryPostVm postVm = new CategoryPostVm("New Category", "new-category", 
            "Desc", null, "meta", "key", (short) 1, true, 1L);
            
        Category result = categoryService.create(postVm);
        
        assertNotNull(result);
        assertEquals("New Category", result.getName());
    }

    @Test
    void create_WhenDuplicateName_ShouldThrowDuplicatedException() {
        CategoryPostVm postVm = new CategoryPostVm("name", "new-category", 
            "Desc", null, "meta", "key", (short) 1, true, 1L);
            
        assertThrows(DuplicatedException.class, () -> categoryService.create(postVm));
    }

    @Test
    void create_WhenParentCategoryNotFound_ShouldThrowBadRequestException() {
        CategoryPostVm postVm = new CategoryPostVm("New Category", "new-category", 
            "Desc", 9999L, "meta", "key", (short) 1, true, 1L); // 9999L là parentId
            
        assertThrows(BadRequestException.class, () -> categoryService.create(postVm));
    }

    @Test
    void update_WhenValidData_ShouldUpdateSuccessfully() {
        CategoryPostVm postVm = new CategoryPostVm("Updated Category", "updated", 
            "Desc", null, "meta", "key", (short) 1, true, 1L);
            
        categoryService.update(postVm, category.getId());
        
        Category updatedCategory = categoryRepository.findById(category.getId()).get();
        assertEquals("Updated Category", updatedCategory.getName());
    }

    @Test
    void update_WhenCategoryNotFound_ShouldThrowNotFoundException() {
        CategoryPostVm postVm = new CategoryPostVm("Updated Category", "updated", 
            "Desc", null, "meta", "key", (short) 1, true, 1L);
            
        assertThrows(NotFoundException.class, () -> categoryService.update(postVm, 9999L));
    }

    @Test
    void update_WhenParentCategoryIsItself_ShouldThrowBadRequestException() {
        CategoryPostVm postVm = new CategoryPostVm("Updated Category", "updated", 
            "Desc", category.getId(), "meta", "key", (short) 1, true, 1L);
            
        assertThrows(BadRequestException.class, () -> categoryService.update(postVm, category.getId()));
    }

    @Test
    void update_WhenParentCategoryNotFound_ShouldThrowBadRequestException() {
        CategoryPostVm postVm = new CategoryPostVm("Updated Category", "updated", 
            "Desc", 9999L, "meta", "key", (short) 1, true, 1L);
            
        assertThrows(BadRequestException.class, () -> categoryService.update(postVm, category.getId()));
    }

    @Test
    void getCategoryByIds_ShouldReturnList() {
        List<CategoryGetVm> result = categoryService.getCategoryByIds(List.of(category.getId()));
        assertEquals(1, result.size());
        assertEquals("name", result.getFirst().name());
    }

    @Test
    void getTopNthCategories_ShouldReturnList() {
        List<String> result = categoryService.getTopNthCategories(10);
        assertNotNull(result);
    }
}
