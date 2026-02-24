package com.yas.location;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

import com.yas.commonlibrary.AbstractControllerIT;
import com.yas.commonlibrary.IntegrationTestConfiguration;
import com.yas.location.model.Country;
import com.yas.location.model.District;
import com.yas.location.model.StateOrProvince;
import com.yas.location.repository.CountryRepository;
import com.yas.location.repository.DistrictRepository;
import com.yas.location.repository.StateOrProvinceRepository;
import com.yas.location.service.AddressService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@Import(IntegrationTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DistrictControllerIT extends AbstractControllerIT {

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private StateOrProvinceRepository stateOrProvinceRepository;
    @Autowired
    private AddressService addressService;

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
        districtRepository.save(District.builder()
            .name("district-1")
            .stateProvince(stateOrProvince)
            .build());
    }

    @AfterEach
    public void clearTestData() {
        districtRepository.deleteAll();
        stateOrProvinceRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void test_getDistrict_shouldReturnData_ifProvideValidStateProvinceId() {
        given(getRequestSpecification())
            .pathParam("id", stateOrProvince.getId())
            .when()
            .get("/v1/storefront/district/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_getDistrict_shouldReturnData_ifProvideValidAccessTokenAndValidStateId() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin","admin"))
            .pathParam("id", stateOrProvince.getId())
            .when()
            .get("/v1/backoffice/district/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_getDistrict_shouldReturn401_ifProvideInvalidAccessToken(){
        given(getRequestSpecification())
            .pathParam("id", stateOrProvince.getId())
            .when()
            .get("/v1/backoffice/district/{id}")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .log().ifValidationFails();
    }
}