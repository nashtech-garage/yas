package com.yas.tax.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.instancio.Select.field;

import com.yas.tax.constants.PageableConstant;
import com.yas.tax.config.IntegrationTestConfiguration;
import com.yas.tax.model.TaxClass;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import io.restassured.RestAssured;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaxClassControllerIT extends AbstractControllerIT{
    final String TAX_CLASS_BASE_URL="/v1/backoffice/tax-classes";
    final String TAX_CLASS_PAGING_URL="/v1/backoffice/tax-classes/paging";
    @Autowired
    TaxClassRepository taxClassRepository;
    @Autowired
    TaxRateRepository taxRateRepository;

    TaxClass taxClass;

    @BeforeEach
    void setUp(){
        taxClass = taxClassRepository.save(Instancio.of(TaxClass.class).create());
    }

    @AfterEach
    void tearDown() {
        taxRateRepository.deleteAll();
        taxClassRepository.deleteAll();
    }

    @Test
    void test_findAllTaxClass_shouldReturnData_whenGivenAccessToken(){
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .when()
            .get(TAX_CLASS_BASE_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_findAllTaxClass_shouldReturn401_whenNotGivenAccessToken(){
        RestAssured.given(getRequestSpecification())
            .when()
            .get(TAX_CLASS_BASE_URL)
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getTaxClass_shouldReturn401_whenUnauthenticated(){
        RestAssured.given(getRequestSpecification())
            .when()
            .get(TAX_CLASS_BASE_URL + "/" + taxClass.getId())
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getTaxClass_shouldReturnData_whenGivenAccessTokenAndCorrectId(){
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .when()
            .get(TAX_CLASS_BASE_URL + "/" + taxClass.getId())
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(taxClass.getName()))
            .log().ifValidationFails();
    }

    @Test
    void test_getTaxClass_shouldReturn404_whenGivenAccessTokenAndWrongId(){
        long wrongId = taxClass.getId() - 1;
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .when()
            .get(TAX_CLASS_BASE_URL + "/" + wrongId)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getPagedTaxClass_shouldReturn401_whenNotGivenAccessToken(){
        RestAssured.given(getRequestSpecification())
            .when()
            .get(TAX_CLASS_PAGING_URL)
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getPagedTaxClass_shouldReturnData_whenGivenAccessToken(){
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .when()
            .get(TAX_CLASS_PAGING_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("pageNo", equalTo(Integer.valueOf(PageableConstant.DEFAULT_PAGE_NUMBER)))
            .body("pageSize", equalTo(Integer.valueOf(PageableConstant.DEFAULT_PAGE_SIZE)))
            .body("totalElements", equalTo(1))
            .body("totalPages", equalTo(1))
            .log().ifValidationFails();
    }

    @Test
    void test_createTaxClass_shouldReturn403_whenNotGivenAccessToken() {
        TaxClassPostVm body = Instancio.of(TaxClassPostVm.class).create();
        RestAssured.given(getRequestSpecification())
            .body(body)
            .post(TAX_CLASS_BASE_URL)
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .log().ifValidationFails();
    }

    @Test
    void test_createTaxClass_shouldReturnCreated_whenGivenAccessToken() {
        TaxClassPostVm body = Instancio.of(TaxClassPostVm.class).create();
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(body)
            .post(TAX_CLASS_BASE_URL)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .log().ifValidationFails();
    }

    @Test
    void test_createTaxClass_shouldReturn400_whenGivenAccessTokenAndExistedName() {
        TaxClassPostVm body = Instancio.of(TaxClassPostVm.class)
            .set(field("name"), taxClass.getName()).create();
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(body)
            .post(TAX_CLASS_BASE_URL)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .log().ifValidationFails();

        assertThat(taxClassRepository.existsByName(body.name())).isTrue();
    }

    @Test
    void test_updateTaxClass_shouldReturn403_whenGivenAccessTokenAndExistedName() {
        TaxClassPostVm body = Instancio.of(TaxClassPostVm.class)
            .set(field("name"), taxClass.getName()).create();

        RestAssured.given(getRequestSpecification())
            .body(body)
            .put(TAX_CLASS_BASE_URL + "/" + taxClass.getId())
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .log().ifValidationFails();
    }

    @Test
    void test_updateTaxClass_shouldReturn204_whenGivenAccessTokenAndCorrectId() {
        TaxClassPostVm body = Instancio.of(TaxClassPostVm.class)
            .set(field("name"), taxClass.getName()).create();

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(body)
            .put(TAX_CLASS_BASE_URL + "/" + taxClass.getId())
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();

        Optional<TaxClass> result = taxClassRepository.findById(taxClass.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(body.name());
    }

    @Test
    void test_updateTaxClass_shouldReturn404_whenGivenAccessTokenAndWrongId() {
        long wrongId = taxClass.getId() - 1;
        TaxClassPostVm body = Instancio.of(TaxClassPostVm.class)
            .set(field("name"), taxClass.getName()).create();

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(body)
            .put(TAX_CLASS_BASE_URL + "/" + wrongId)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_updateTaxClass_shouldReturn400_whenGivenAccessTokenAndDuplicateName() {
        TaxClass anotherClass = taxClassRepository.save(Instancio.of(TaxClass.class).create());

        TaxClassPostVm body = Instancio.of(TaxClassPostVm.class)
            .set(field("name"), taxClass.getName()).create();

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(body)
            .put(TAX_CLASS_BASE_URL + "/" + anotherClass.getId())
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteTaxClass_shouldReturn403_whenGivenAccessTokenAndExistedName() {
        RestAssured.given(getRequestSpecification())
            .delete(TAX_CLASS_BASE_URL + "/" + taxClass.getId())
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteTaxClass_shouldReturn204_whenGivenAccessTokenAndCorrectId() {
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .delete(TAX_CLASS_BASE_URL + "/" + taxClass.getId())
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteTaxClass_shouldReturn404_whenGivenAccessTokenAndWrongId() {
        long wrongId = taxClass.getId() - 1;

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .delete(TAX_CLASS_BASE_URL + "/" + wrongId)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }
}
