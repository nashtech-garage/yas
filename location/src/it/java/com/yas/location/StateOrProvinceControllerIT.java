package com.yas.location;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.yas.commonlibrary.AbstractControllerIT;
import com.yas.commonlibrary.IntegrationTestConfiguration;
import com.yas.location.model.Country;
import com.yas.location.model.StateOrProvince;
import com.yas.location.repository.CountryRepository;
import com.yas.location.repository.StateOrProvinceRepository;
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
public class StateOrProvinceControllerIT extends AbstractControllerIT {

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private StateOrProvinceRepository stateOrProvinceRepository;
    private Country country;
    private StateOrProvince stateOrProvince;

    @BeforeEach
    public void insertTestData() {
        country = countryRepository.save(Country.builder()
            .name("country-1")
            .build());
        stateOrProvince = stateOrProvinceRepository.save(StateOrProvince.builder()
            .name("state-or-province")
            .country(country)
            .build());
    }

    @AfterEach
    public void clearTestData() {
        stateOrProvinceRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void test_getAllByCountryId_shouldReturnData_ifProvideValidAccessTokenAndValidId() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .param("countryId", country.getId())
            .when()
            .get("/v1/backoffice/state-or-provinces")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_getAllCountryId_shouldReturn401_ifProvideInvalidAccessToken() {
        given(getRequestSpecification())
            .pathParam("id", country.getId())
            .when()
            .get("/v1/backoffice/state-or-provinces/{id}")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getState_shouldReturnData_ifProvideValidAccessTokenAndValidId() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", stateOrProvince.getId())
            .when()
            .get("/v1/backoffice/state-or-provinces/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(stateOrProvince.getName()))
            .log().ifValidationFails();
    }

    @Test
    void test_getState_shouldReturn401_ifProvideInvalidAccessToken() {
        given(getRequestSpecification())
            .pathParam("id", stateOrProvince.getId())
            .when()
            .get("/v1/backoffice/state-or-provinces/{id}")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getStateWithCountryName_shouldReturnData_ifProvideValidAccessAndValidId() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .param("stateOrProvinceIds", List.of(stateOrProvince.getId()))
            .when()
            .get("/v1/backoffice/state-or-provinces/state-country-names")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_getStateWithCountryName_shouldReturn401_ifProvideInvalidAccessToken(){
        given(getRequestSpecification())
            .param("stateOrProvinceIds", List.of(stateOrProvince.getId()))
            .when()
            .get("/v1/backoffice/state-or-provinces/state-country-names")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteState_shouldDelete_ifProvideValidAccessAndValidId() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", stateOrProvince.getId())
            .when()
            .delete("/v1/backoffice/state-or-provinces/{id}")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteState_shouldReturn403_ifProvideInvalidAccessToken() {
        given(getRequestSpecification())
            .pathParam("id", stateOrProvince.getId())
            .when()
            .delete("/v1/backoffice/state-or-provinces/{id}")
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getAllPagination_shouldReturnData_ifProvideValidAccessTokenAndValidId() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .param("pageNo", 0)
            .param("pageSize", 1)
            .param("countryId", country.getId())
            .when()
            .get("/v1/backoffice/state-or-provinces/paging")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("stateOrProvinceContent", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_getState_shouldReturnData_ifProvideValidId() {
        given(getRequestSpecification())
            .pathParam("countryId", country.getId())
            .when()
            .get("/v1/storefront/state-or-provinces/{countryId}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .log().ifValidationFails();
    }
}
