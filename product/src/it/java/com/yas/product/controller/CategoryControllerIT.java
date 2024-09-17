package com.yas.product.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.yas.product.config.AbstractControllerIT;
import com.yas.product.config.IntegrationTestConfiguration;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.service.CategoryService;
import com.yas.product.viewmodel.category.CategoryPostVm;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerIT extends AbstractControllerIT {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private ProductRepository productRepository;

    private Category categoryOne;
    private Category categoryTwo;

    private static final String CATEGORY_BACKOFFICE_URL = "/v1/backoffice/categories";
    private static final String CATEGORY_STOREFRONT_URL = "/v1/storefront/categories";

    @BeforeEach
    public void insertTestData() {
        categoryOne = new Category();
        categoryOne.setName("Category-1");
        categoryOne.setDescription("Des-1");
        categoryOne.setSlug("Slug-1");
        categoryOne.setDisplayOrder((short) 1);

        categoryTwo = new Category();
        categoryTwo.setName("Category-2");
        categoryTwo.setDescription("Des-2");
        categoryTwo.setSlug("Slug-2");
        categoryOne.setDisplayOrder((short) 1);

        categoryRepository.saveAll(List.of(categoryOne, categoryTwo));
    }

    @AfterEach
    public void clearTestData() {
        categoryRepository.deleteAll();
    }

    @Test
    void test_getAllCategoriesBackoffice_shouldReturnListCategories() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .param("categoryName", "a")
            .when()
            .get(CATEGORY_BACKOFFICE_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(2))
            .log().ifValidationFails();
    }

    @Test
    void test_getAllCategoriesStorefront_shouldReturnListCategories() {
        given(getRequestSpecification())
            .param("categoryName", "a")
            .when()
            .get(CATEGORY_STOREFRONT_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(2))
            .log().ifValidationFails();
    }

    @Test
    void test_getCategoryById_shouldReturnCategory() {
        Long categoryId = categoryOne.getId();
        String name = categoryOne.getName();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", categoryId)
            .when()
            .get(CATEGORY_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(name))
            .log().ifValidationFails();
    }

    @Test
    void test_getCategoryByIdWithParent_shouldReturnCategory() {
        categoryOne.setParent(categoryTwo);
        categoryRepository.save(categoryOne);
        Long categoryId = categoryOne.getId();
        String name = categoryOne.getName();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", categoryId)
            .when()
            .get(CATEGORY_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(name))
            .log().ifValidationFails();
    }

    @Test
    void test_getCategoryById_shouldReturn404_whenNotFound() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", 10000L)
            .when()
            .get(CATEGORY_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_createCategory_shouldSuccess_whenProvideValidData() {
        Long parentId = categoryTwo.getId();
        CategoryPostVm categoryPostVm = new CategoryPostVm("Electronics",
            "electronics",
            "Category for all electronic items",
            parentId,
            "electronics, gadgets, tech",
            "Latest and greatest in electronics",
            (short) 1,
            true,
            456L);
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .body(categoryPostVm)
            .when()
            .post(CATEGORY_BACKOFFICE_URL)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("name", equalTo(categoryPostVm.name()))
            .log().ifValidationFails();
    }

    @Test
    void test_createCategoryWithNameExist_shouldReturnError() {
        Long parentId = categoryTwo.getId();
        CategoryPostVm categoryPostVm = new CategoryPostVm("Category-1",
            "electronics",
            "Category for all electronic items",
            parentId,
            "electronics, gadgets, tech",
            "Latest and greatest in electronics",
            (short) 1,
            true,
            456L);
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .body(categoryPostVm)
            .when()
            .post(CATEGORY_BACKOFFICE_URL)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .log().ifValidationFails();
    }

    @Test
    void test_createCategoryWithParentNull_shouldSuccess_whenProvideValidData() {
        CategoryPostVm categoryPostVm = new CategoryPostVm("Electronics",
            "electronics",
            "Category for all electronic items",
            null,
            "electronics, gadgets, tech",
            "Latest and greatest in electronics",
            (short) 1,
            true,
            456L);
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .body(categoryPostVm)
            .when()
            .post(CATEGORY_BACKOFFICE_URL)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("name", equalTo(categoryPostVm.name()))
            .log().ifValidationFails();
    }

    @Test
    void test_createCategoryWithParentNotFound_shouldReturn400() {
        CategoryPostVm categoryPostVm = new CategoryPostVm("Electronics",
            "electronics",
            "Category for all electronic items",
            123L,
            "electronics, gadgets, tech",
            "Latest and greatest in electronics",
            (short) 1,
            true,
            456L);
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .body(categoryPostVm)
            .when()
            .post(CATEGORY_BACKOFFICE_URL)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .log().ifValidationFails();
    }

    @Test
    void test_updateCategoryWithParentNull_shouldSuccess_whenProvideValidData() {
        CategoryPostVm categoryPostVm = new CategoryPostVm("Electronics",
            "electronics",
            "Category for all electronic items",
            null,
            "electronics, gadgets, tech",
            "Latest and greatest in electronics",
            (short) 1,
            true,
            456L);
        Long categoryId = categoryOne.getId();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", categoryId)
            .body(categoryPostVm)
            .when()
            .put(CATEGORY_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();
    }

    @Test
    void test_updateCategoryWithParentNotFound_shouldReturn400() {
        CategoryPostVm categoryPostVm = new CategoryPostVm("Electronics",
            "electronics",
            "Category for all electronic items",
            123L,
            "electronics, gadgets, tech",
            "Latest and greatest in electronics",
            (short) 1,
            true,
            456L);
        Long categoryId = categoryOne.getId();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", categoryId)
            .body(categoryPostVm)
            .when()
            .put(CATEGORY_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .log().ifValidationFails();
    }

    @Test
    void test_updateCategory_shouldSuccess_whenProvideValidData() {
        categoryOne.setParent(categoryTwo);
        categoryRepository.save(categoryOne);
        Long parentId = categoryTwo.getId();
        CategoryPostVm categoryPostVm = new CategoryPostVm("Electronics",
            "electronics",
            "Category for all electronic items",
            parentId,
            "electronics, gadgets, tech",
            "Latest and greatest in electronics",
            (short) 1,
            true,
            456L);
        Long categoryId = categoryOne.getId();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", categoryId)
            .body(categoryPostVm)
            .when()
            .put(CATEGORY_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();
    }

    @Test
    void test_updateCategoryWithParentItSelf_shouldReturn400() {
        Long categoryId = categoryOne.getId();
        CategoryPostVm categoryPostVm = new CategoryPostVm("Electronics",
            "electronics",
            "Category for all electronic items",
            categoryId,
            "electronics, gadgets, tech",
            "Latest and greatest in electronics",
            (short) 1,
            true,
            456L);
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", categoryId)
            .body(categoryPostVm)
            .when()
            .put(CATEGORY_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .log().ifValidationFails();
    }

    @Test
    void test_updateCategory_shouldReturn404_ifNotFound() {
        Long parentId = categoryTwo.getId();
        CategoryPostVm categoryPostVm = new CategoryPostVm("Electronics",
            "electronics",
            "Category for all electronic items",
            parentId,
            "electronics, gadgets, tech",
            "Latest and greatest in electronics",
            (short) 1,
            true,
            456L);
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", 1000L)
            .body(categoryPostVm)
            .when()
            .put(CATEGORY_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteCategory_shouldSuccess_whenProvideValidId() {
        Long categoryId = categoryOne.getId();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", categoryId)
            .when()
            .delete(CATEGORY_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteCategory_shouldReturn404_whenNotFound() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", 10000L)
            .when()
            .delete(CATEGORY_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteCategory_shouldReturn400_whenContainsChildren() {
        categoryOne.setCategories(new ArrayList<>(List.of(categoryTwo)));
        categoryTwo.setParent(categoryOne);

        categoryRepository.save(categoryTwo);
        categoryRepository.save(categoryOne);
        Long categoryId = categoryOne.getId();

        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", categoryId)
            .when()
            .delete(CATEGORY_BACKOFFICE_URL + "/{id}")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getAllCategoriesByIds_shouldReturnListCategories() {
        Long categoryOneId = categoryOne.getId();
        Long categoryTwoId = categoryTwo.getId();
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .param("ids", new ArrayList<>(List.of(categoryOneId, categoryTwoId)))
            .when()
            .get(CATEGORY_BACKOFFICE_URL + "/by-ids")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(2))
            .log().ifValidationFails();
    }
}
