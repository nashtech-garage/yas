package com.yas.product.controller;

import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.viewmodel.category.CategoryGetDetailVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;
import com.yas.product.viewmodel.error.ErrorVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CategoryControllerTest extends BaseControllerTest {
    @Autowired
    private CategoryRepository categoryRepository;
    private Category category;
    private Long categoryId;
    private final String STORE_FRONT_URL = "/storefront/categories";
    private final String BACK_OFFICE_URL = "/backoffice/categories";
    private final Long invalidId = 9999L;

    @BeforeEach
    void setUp() {
        super.setup();
        category = new Category();
        category.setName("laptop");
        category.setSlug("laptop-slug");
        category.setMetaKeyword("keyword");
        category.setMetaDescription("description");
        category.setDisplayOrder((short) 0);
        categoryId = categoryRepository.save(category).getId();
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }

    @Test
    void getListCategories_StoreFront_Success() {
        EntityExchangeResult<List<CategoryGetVm>> result =
                webTestClient.get().uri(STORE_FRONT_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBodyList(CategoryGetVm.class)
                        .returnResult();
        List<CategoryGetVm> brandVms = result.getResponseBody();
        assertEquals(1, brandVms.size());
        assertEquals(category.getName(), brandVms.get(0).name());
        assertEquals(category.getSlug(), brandVms.get(0).slug());
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void getListCategories_Backoffice_Success() {
        EntityExchangeResult<List<CategoryGetVm>> result =
                webTestClient.get().uri(BACK_OFFICE_URL).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CategoryGetVm.class)
                .returnResult();
        List<CategoryGetVm> categoryGetVms = result.getResponseBody();
        assertEquals(1, categoryGetVms.size());
        assertEquals(category.getName(), categoryGetVms.get(0).name());
        assertEquals(category.getSlug(), categoryGetVms.get(0).slug());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void getCategory_NotFoundCategoryGetDetailVM_404NotFound() {
        ErrorVm errorVmExpected = new ErrorVm("404 NOT_FOUND", "Not Found", "Category 9999 is not found", Collections.emptyList());
        webTestClient.get().uri(BACK_OFFICE_URL + "/{id}", invalidId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void getCategory_ValidCategoryGetDetailVM_Success() {
        EntityExchangeResult<CategoryGetDetailVm> result =
                webTestClient.get().uri(BACK_OFFICE_URL + "/{id}", categoryId)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(CategoryGetDetailVm.class)
                        .returnResult();
        CategoryGetDetailVm categoryGetDetailVm = result.getResponseBody();
        assertNotNull(categoryGetDetailVm);
        assertEquals(category.getName(), categoryGetDetailVm.name());
        assertEquals(category.getSlug(), categoryGetDetailVm.slug());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void createCategory_ValidCategoryWithOutParentId_Success() {
        CategoryPostVm categoryPostVm = new CategoryPostVm(
                "laptop2", "laptop-slug2", "description2",
                null, "", "", (short) 0, true, 1L);
        EntityExchangeResult<CategoryGetDetailVm> result = webTestClient.post().uri(BACK_OFFICE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(categoryPostVm))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CategoryGetDetailVm.class)
                .returnResult();
        CategoryGetDetailVm categoryGetDetailVm = result.getResponseBody();
        assertNotNull(categoryGetDetailVm);
        assertEquals(categoryPostVm.name(), categoryGetDetailVm.name());
        assertEquals(categoryPostVm.slug(), categoryGetDetailVm.slug());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void createCategory_ValidCategoryWithParentId_Success() {
        CategoryPostVm categoryPostVm = new CategoryPostVm(
                "laptop2", "laptop-slug2", "description2",
                categoryId, "", "", (short) 0, true, 1L);
        EntityExchangeResult<CategoryGetDetailVm> result = webTestClient.post().uri(BACK_OFFICE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(categoryPostVm))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CategoryGetDetailVm.class)
                .returnResult();
        CategoryGetDetailVm categoryGetDetailVm = result.getResponseBody();
        assertNotNull(categoryGetDetailVm);
        assertEquals(categoryPostVm.name(), categoryGetDetailVm.name());
        assertEquals(categoryPostVm.slug(), categoryGetDetailVm.slug());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void createCategory_ValidCategoryWithNotFoundParentId_400BadRequest() {
        CategoryPostVm categoryPostVm = new CategoryPostVm(
                "laptop2", "laptop-slug2", "description2",
                9999L, "", "", (short) 0, true, 1L);
        ErrorVm errorVmExpected = new ErrorVm("400 BAD_REQUEST", "Bad Request", "Parent category 9999 is not found", Collections.emptyList());
        webTestClient.post().uri(BACK_OFFICE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(categoryPostVm))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }
}