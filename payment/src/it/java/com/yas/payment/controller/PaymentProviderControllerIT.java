package com.yas.payment.controller;

import com.yas.payment.config.IntegrationTestConfiguration;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.repository.PaymentProviderRepository;
import io.restassured.RestAssured;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.instancio.Select.field;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentProviderControllerIT extends AbstractControllerIT {

    private static final String PAYMENT_PROVIDERS_URL = "v1/storefront/payment-providers";
    private static final String ADDITIONAL_SETTINGS_URL_TEMPLATE = "v1/payment-providers/{id}/additional-settings";

    @Autowired
    PaymentProviderRepository paymentProviderRepository;

    PaymentProvider paymentProvider;

    @BeforeEach
    void setUp() {
        paymentProvider = Instancio.of(PaymentProvider.class)
            .set(field(PaymentProvider::isEnabled), true)
            .create();
        paymentProvider = paymentProviderRepository.save(paymentProvider);
    }

    @AfterEach
    void tearDown() {
        paymentProviderRepository.deleteAll();
    }

    @Test
    void test_getPaymentProviders_shouldReturnPaymentProviders() {
        RestAssured.given(getRequestSpecification())
            .when()
            .get(PAYMENT_PROVIDERS_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(1))
            .log().ifValidationFails();
    }

    @Test
    void test_getAdditionalSettings_shouldReturnAdditionalSettings() {
        RestAssured.given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"))
            .pathParam("id", paymentProvider.getId())
            .when()
            .get(ADDITIONAL_SETTINGS_URL_TEMPLATE)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo(paymentProvider.getAdditionalSettings()))
            .log().ifValidationFails();
    }

    @Test
    void test_getAdditionalSettings_shouldReturn404_whenInvalidId() {
        String invalidId = UUID.randomUUID().toString();

        RestAssured.given(getRequestSpecification())
            .pathParam("id", invalidId)
            .when()
            .get(ADDITIONAL_SETTINGS_URL_TEMPLATE)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .log().ifValidationFails();
    }
}
