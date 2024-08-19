package com.yas.tax.integration.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.tax.integration.config.IntegrationTestConfiguration;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.service.LocationService;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import io.restassured.RestAssured;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaxRateControllerIntegrationTest extends AbstractControllerIT {

    @Autowired
    TaxRateRepository taxRateRepository;

    @Autowired
    TaxClassRepository taxClassRepository;

    @MockBean
    LocationService locationService;

    TaxClass taxClass;
    TaxRate taxRate;

    @BeforeEach
    void setUp() {
        taxClass = taxClassRepository.save(Instancio.of(TaxClass.class).create());
        taxRate = taxRateRepository.save(Instancio.of(TaxRate.class)
            .set(field("taxClass"), taxClass)
            .create());
    }

    @AfterEach
    void tearDown() {
        taxRateRepository.deleteAll();
        taxClassRepository.deleteAll();
    }

    @Test
    void test_getTaxRate_shouldReturn401_whenNotGivenAccessToken() {
        RestAssured.given(getRequestSpecification())
            .when()
            .get("/v1/backoffice/tax-rates" + taxRate.getId().toString())
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getTaxRate_shouldReturnData_whenGivenAccessToken() {
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .when()
            .get("/v1/backoffice/tax-rates/" + taxRate.getId().toString())
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", comparesEqualTo(Integer.valueOf(taxRate.getId().toString())))
            .log().ifValidationFails();
    }

    @Test
    void test_getTaxRate_shouldReturn404_whenGivenAccessTokenAndWrongId() {
        long wrongId = taxRate.getId() + 1;

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .when()
            .get("/v1/backoffice/tax-rates/" + wrongId)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_createTaxRate_shouldReturn403_whenNotGivenAccessToken() {
        TaxRatePostVm body = Instancio.of(TaxRatePostVm.class)
            .set(field("taxClassId"), taxClass.getId()).create();

        RestAssured.given(getRequestSpecification())
            .body(body)
            .post("/v1/backoffice/tax-rates")
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .log().ifValidationFails();
    }

    @Test
    void test_createTaxClass_shouldReturnCreated_whenGivenAccessToken() {
        TaxRatePostVm body = Instancio.of(TaxRatePostVm.class)
            .set(field("taxClassId"), taxClass.getId()).create();

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(body)
            .post("/v1/backoffice/tax-rates")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .log().ifValidationFails();
    }

    @Test
    void test_createTaxClass_shouldReturn400_whenGivenAccessTokenAndMissingData() {
        TaxRatePostVm body = Instancio.of(TaxRatePostVm.class)
            .set(field("taxClassId"), taxClass.getId())
            .ignore(field("countryId")).create();

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(body)
            .post("/v1/backoffice/tax-rates")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .log().ifValidationFails();
    }

    @Test
    void test_createTaxClass_shouldReturn400_whenGivenAccessTokenAndTaxClassNotExists() {
        TaxRatePostVm body = Instancio.of(TaxRatePostVm.class)
            .set(field("taxClassId"), Instancio.of(Long.class).create())
            .create();

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(body)
            .post("/v1/backoffice/tax-rates")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_updateTaxRate_shouldReturn403_whenNotGivenAccessToken() {
        TaxRatePostVm body = Instancio.of(TaxRatePostVm.class)
            .set(field("taxClassId"), taxClass.getId()).create();

        RestAssured.given(getRequestSpecification())
            .body(body)
            .put("/v1/backoffice/tax-rates/"+taxRate.getId().toString())
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .log().ifValidationFails();
    }

    @Test
    void test_updateTaxClass_shouldReturnNoContent_whenUpdateSuccessfully() {
        TaxRatePostVm body = Instancio.of(TaxRatePostVm.class)
            .set(field("taxClassId"), taxClass.getId()).create();

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(body)
            .put("/v1/backoffice/tax-rates/"+taxRate.getId().toString())
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();
    }

    @Test
    void test_updateTaxClass_shouldReturn404_whenGivenAccessTokenAndWrongId() {
        TaxRatePostVm body = Instancio.of(TaxRatePostVm.class)
            .set(field("taxClassId"), taxClass.getId()).create();

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(body)
            .put("/v1/backoffice/tax-rates/"+Instancio.create(Integer.class).toString())
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_updateTaxClass_shouldReturn400_whenGivenAccessTokenAndMissingData() {
        TaxRatePostVm body = Instancio.of(TaxRatePostVm.class)
            .set(field("taxClassId"), taxClass.getId())
            .ignore(field("countryId")).create();

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(body)
            .put("/v1/backoffice/tax-rates/"+taxRate.getId().toString())
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .log().ifValidationFails();
    }

    @Test
    void test_updateTaxClass_shouldReturn400_whenGivenAccessTokenAndTaxClassNotExists() {
        TaxRatePostVm body = Instancio.of(TaxRatePostVm.class)
            .set(field("taxClassId"), Instancio.of(Long.class).create())
            .create();

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .body(body)
            .put("/v1/backoffice/tax-rates/"+taxRate.getId().toString())
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteTaxRate_shouldReturn401_whenNotGivenAccessToken() {
        RestAssured.given(getRequestSpecification())
            .when()
            .delete("/v1/backoffice/tax-rates" + taxRate.getId().toString())
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteTaxRate_shouldReturn204_whenGivenAccessToken() {
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .when()
            .delete("/v1/backoffice/tax-rates/" + taxRate.getId().toString())
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteTaxRate_shouldReturn404_whenGivenAccessTokenAndWrongId() {
        long wrongId = taxRate.getId() + 1;

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .when()
            .delete("/v1/backoffice/tax-rates/" + wrongId)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getPagedTaxRate_shouldReturn401_whenNotGivenAccessToken() {
        RestAssured.given(getRequestSpecification())
            .when()
            .get("/v1/backoffice/tax-rates/paging")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getPagedTaxRate_shouldReturnData_whenGivenAccessToken() {
        StateOrProvinceAndCountryGetNameVm stateOrProvinceAndCountryGetNameVm =
            Instancio.of(StateOrProvinceAndCountryGetNameVm.class)
                .set(field("stateOrProvinceId"), taxRate.getStateOrProvinceId())
                .create();
        when(locationService.getStateOrProvinceAndCountryNames(any()))
            .thenReturn(List.of(stateOrProvinceAndCountryGetNameVm));

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .when()
            .get("/v1/backoffice/tax-rates/paging")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("pageNo", equalTo(0))
            .body("pageSize", equalTo(10))
            .body("totalElements", equalTo(1))
            .body("isLast", equalTo(true))
            .log().ifValidationFails();
    }

    @Test
    void test_getPagedTaxRate_shouldReturnEmptyData_whenGivenAccessTokenAndDBIsEmpty() {
        StateOrProvinceAndCountryGetNameVm stateOrProvinceAndCountryGetNameVm =
            Instancio.of(StateOrProvinceAndCountryGetNameVm.class)
                .set(field("stateOrProvinceId"), taxRate.getStateOrProvinceId())
                .create();
        when(locationService.getStateOrProvinceAndCountryNames(any()))
            .thenReturn(List.of(stateOrProvinceAndCountryGetNameVm));
        taxRateRepository.deleteAll();

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .when()
            .get("/v1/backoffice/tax-rates/paging")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("pageNo", equalTo(0))
            .body("pageSize", equalTo(10))
            .body("totalElements", equalTo(0))
            .body("isLast", equalTo(true))
            .log().ifValidationFails();
    }

    @Test
    void test_getPercentTaxRate_shouldReturn401_whenNotGivenAccessToken() {
        RestAssured.given(getRequestSpecification())
            .param("taxClassId", taxRate.getTaxClass().getId())
            .param("countryId", taxRate.getCountryId())
            .param("stateOrProvinceId", taxRate.getStateOrProvinceId())
            .param("zipCode", taxRate.getZipCode())
            .when()
            .get("/v1/backoffice/tax-rates/tax-percent")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getPercentTaxRate_shouldReturnData_whenGivenAccessToken() {
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .queryParam("taxClassId", taxRate.getTaxClass().getId())
            .queryParam("countryId", taxRate.getCountryId())
            .param("stateOrProvinceId", taxRate.getStateOrProvinceId())
            .param("zipCode", taxRate.getZipCode())
            .when()
            .get("/v1/backoffice/tax-rates/tax-percent")
            .then()
            .statusCode(HttpStatus.OK.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getPercentTaxRate_shouldReturn400_whenGivenAccessTokenAndNotCountryId() {
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .param("taxClassId", taxRate.getTaxClass().getId())
            .when()
            .get("/v1/backoffice/tax-rates/tax-percent")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getPercentTaxRate_shouldReturnData_whenGivenAccessTokenAndNoTaxClassId() {
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .param("countryId", taxRate.getCountryId())
            .when()
            .get("/v1/backoffice/tax-rates/tax-percent")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getPercentTaxRate_shouldReturnData0_whenGivenAccessTokenAndDataNotExist() {
        when(locationService.getStateOrProvinceAndCountryNames(any()))
            .thenReturn(Instancio.ofList(StateOrProvinceAndCountryGetNameVm.class)
                .size(1).create());

        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .param("taxClassId", taxRate.getTaxClass().getId())
            .param("countryId", Instancio.create(Integer.class))
            .when()
            .get("/v1/backoffice/tax-rates/tax-percent")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", comparesEqualTo(0.0F))
            .log().ifValidationFails();
    }
}