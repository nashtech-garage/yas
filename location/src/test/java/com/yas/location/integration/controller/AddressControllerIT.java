package com.yas.location.integration.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.yas.location.integration.config.AbstractControllerIT;
import com.yas.location.integration.config.IntegrationTestConfiguration;
import com.yas.location.model.Address;
import com.yas.location.model.Country;
import com.yas.location.model.District;
import com.yas.location.model.StateOrProvince;
import com.yas.location.repository.AddressRepository;
import com.yas.location.repository.CountryRepository;
import com.yas.location.repository.DistrictRepository;
import com.yas.location.repository.StateOrProvinceRepository;
import com.yas.location.service.AddressService;
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
public class AddressControllerIT extends AbstractControllerIT {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private StateOrProvinceRepository stateOrProvinceRepository;
    @Autowired
    private AddressService addressService;

    private Address address;
    private Country country;
    private District district;
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
        district = districtRepository.save(District.builder()
            .name("district-1")
            .stateProvince(stateOrProvince)
            .build());
        address = addressRepository.save(Address.builder()
            .contactName("address-1")
            .city("city-1")
            .country(country)
            .district(district)
            .stateOrProvince(stateOrProvince)
            .build());
    }

    @AfterEach
    public void clearTestData() {
        addressRepository.deleteAll();
        districtRepository.deleteAll();
        stateOrProvinceRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void test_getAddress_shouldReturnData_ifProvideValidId() {
        given(getRequestSpecification())
            .pathParam("id", address.getId())
            .when()
            .get("/v1/storefront/addresses/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("contactName", equalTo(address.getContactName()))
            .log().ifValidationFails();
    }

    @Test
    void test_getAllAddresses_shouldReturnData() {
        given(getRequestSpecification())
            .when()
            .get("/v1/storefront/addresses?ids=" + address.getId().toString())
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_deleteAddress_shouldOk_ifProvideValidAccessTokenAndValidId() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .pathParam("id", address.getId())
            .when()
            .delete("/v1/storefront/addresses/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteAddress_shouldReturn403_ifProvideInvalidAccessToken() {
        given(getRequestSpecification())
            .pathParam("id", address.getId())
            .when()
            .delete("/v1/storefront/addresses/{id}")
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .log().ifValidationFails();
    }

    @Test
    void test_deleteAddress_shouldReturn404_ifProvideAccessTokenAndInvalidId() {
        given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .pathParam("id", 1000L)
            .when()
            .delete("/v1/storefront/addresses/{id}")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }
}
