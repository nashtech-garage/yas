package com.yas.product.controller;

import com.yas.product.ProductApplication;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.service.CategoryService;
import com.yas.product.viewmodel.ImageVm;
import com.yas.product.viewmodel.category.CategoryGetDetailVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CategoryController.class)
@ContextConfiguration(classes = ProductApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private CategoryService categoryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testListCategories() throws Exception {
        CategoryGetVm category1 =
                new CategoryGetVm(2L, "Category 1", "category-1", 1L, new ImageVm(2L, ""));
        CategoryGetVm category2 =
                new CategoryGetVm(3L, "Category 2", "category-2", 1L, new ImageVm(3L, ""));

        when(categoryService.getCategories(any())).thenReturn(Arrays.asList(category1, category2));

        mockMvc.perform(get("/backoffice/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].name").value("Category 1"))
                .andExpect(jsonPath("$[1].id").value(3L))
                .andExpect(jsonPath("$[1].name").value("Category 2"));
    }

    @Test
    void testGetCategory() throws Exception {
        CategoryGetDetailVm categoryDetail = new CategoryGetDetailVm(
                123L, "Electronics", "electronics",
                "A category for electronic products.", 1L,
                "electronics, gadgets, technology", "A category for electronic products.",
                (short) 1, true, new ImageVm(1L, "image-url")
        );
        when(categoryService.getCategoryById(1L)).thenReturn(categoryDetail);

        mockMvc.perform(get("/backoffice/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123L))
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    void testCreateCategory() throws Exception {
        CategoryPostVm categoryPostVm = new CategoryPostVm("Electronic", "Electronics", "electronics",
                1L,
                "electronics, gadgets, technology", "A category for electronic products.",
                (short) 1, true, 1L);
        Category category = createCategory();

        when(categoryService.create(any(CategoryPostVm.class))).thenReturn(category);

        mockMvc.perform(post("/backoffice/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryPostVm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Electronic"));
    }

    @Test
    void testUpdateCategory() throws Exception {
        CategoryPostVm categoryPostVm = new CategoryPostVm("Electronic", "Electronics",
                "electronics", 1L, "electronics, gadgets, technology",
                "A category for electronic products.",
                (short) 1, true, 1L);

        doNothing().when(categoryService).update(any(CategoryPostVm.class), anyLong());

        mockMvc.perform(put("/backoffice/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryPostVm)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCategory() throws Exception {
        Category category = createCategory();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        doNothing().when(categoryRepository).deleteById(1L);

        mockMvc.perform(delete("/backoffice/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCategoryBadRequest() throws Exception {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/backoffice/categories/1"))
                .andExpect(status().isBadRequest());
    }

    private Category createCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronic");
        category.setDescription("electronics, gadgets, technology");
        category.setDisplayOrder((short) 1);
        return category;
    }
}
