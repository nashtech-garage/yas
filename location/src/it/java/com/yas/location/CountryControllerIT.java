package com.yas.location;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.yas.commonlibrary.AbstractControllerIT;
import com.yas.commonlibrary.IntegrationTestConfiguration;
import com.yas.location.model.Country;
import com.yas.location.repository.CountryRepository;
import com.yas.location.service.CountryService;
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
public class CountryControllerIT extends AbstractControllerIT {

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CountryService countryService;
    private Country country;

    @BeforeEach
    public void insertTestData() {
        country = countryRepository.save(Country.builder()
            .name("country-1")
            .build());
    }

    @AfterEach
    public void clearTestData() {
        countryRepository.deleteAll();
    }

    @Test
    void test_getAllCountries_shouldReturnData_ifProvideValidAccessToken() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .when()
            .get("/v1/backoffice/countries")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_getCountry_shouldReturnData_ifProvideValidAccessTokenAndValidId() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .pathParam("id", country.getId())
            .when()
            .get("/v1/backoffice/countries/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo(country.getName()))
            .log().ifValidationFails();
    }

    @Test
    void test_getCountry_shouldReturn404_ifProvideValidAccessTokenButCountryNotFound() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .pathParam("id", 1000L)
            .when()
            .get("/v1/backoffice/countries/{id}")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteCountry_shouldDelete_ifProvideValidAccessTokenAndValidId() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", country.getId())
            .when()
            .delete("/v1/backoffice/countries/{id}")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteCountry_shouldReturn403_ifProvideInvalidAccessToken() {
        given(getRequestSpecification())
            .pathParam("id", country.getId())
            .when()
            .delete("/v1/backoffice/countries/{id}")
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteCountry_shouldReturn404_ifProvideValidAccessTokenAndInvalidId() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", 1000L)
            .when()
            .delete("/v1/backoffice/countries/{id}")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getPaginateCountry_shouldReturnData_IfProvideValidAccessToken(){
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .param("pageNo", 1)
            .param("pageSize", 1)
            .when()
            .get("/v1/backoffice/countries/paging")
            .then()
            .statusCode(HttpStatus.OK.value())
            .log().ifValidationFails();
    }

    @Test
    void test_getCountries_shouldReturnData() {
        given(getRequestSpecification())
            .when()
            .get("/v1/storefront/countries")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(1))
            .log().ifValidationFails();
    }
}
