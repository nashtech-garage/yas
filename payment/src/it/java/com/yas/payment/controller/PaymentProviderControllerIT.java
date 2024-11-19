package com.yas.payment.controller;

import com.yas.commonlibrary.AbstractControllerIT;
import com.yas.commonlibrary.IntegrationTestConfiguration;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.repository.PaymentProviderRepository;
import com.yas.payment.service.MediaService;
import io.restassured.RestAssured;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.hasSize;
import static org.instancio.Select.field;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentProviderControllerIT extends AbstractControllerIT {

    private static final String PAYMENT_PROVIDERS_URL = "v1/storefront/payment-providers";

    @Autowired
    PaymentProviderRepository paymentProviderRepository;

    @MockBean
    MediaService mediaService;

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
    @Disabled
    void test_getPaymentProviders_shouldReturnPaymentProviders() {
        RestAssured.given(getRequestSpecification())
            .when()
            .get(PAYMENT_PROVIDERS_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(".", hasSize(1))
            .log()
            .ifValidationFails();
    }
}
