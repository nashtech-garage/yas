package com.yas.payment.controller;

import com.yas.commonlibrary.AbstractControllerIT;
import com.yas.commonlibrary.IntegrationTestConfiguration;
import com.yas.payment.model.Payment;
import com.yas.payment.viewmodel.CheckoutPaymentVm;
import com.yas.payment.repository.PaymentRepository;
import com.yas.payment.service.OrderService;
import com.yas.payment.viewmodel.CapturedPayment;
import com.yas.payment.viewmodel.PaymentOrderStatusVm;
import io.restassured.RestAssured;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentControllerIT extends AbstractControllerIT {

    private static final String PAYMENT_CAPTURE_URL = "/v1/storefront/payments/capture";
    private static final String EVENT_CREATE_PAYMENT_URL = "/v1/events/payments";

    @Autowired
    PaymentRepository paymentRepository;

    @MockBean
    OrderService orderService;

    Payment payment;
    CapturedPayment capturedPayment;
    CheckoutPaymentVm checkoutPaymentRequest;

    @BeforeEach
    void setUp() {
        payment = paymentRepository.save(Instancio.of(Payment.class).create());
        capturedPayment = Instancio.of(CapturedPayment.class).create();
        checkoutPaymentRequest = Instancio.of(CheckoutPaymentVm.class).create();

        Mockito.when(orderService.updateCheckoutStatus(Mockito.any(CapturedPayment.class)))
            .thenAnswer(invocation -> Mockito.anyLong());

        Mockito.when(orderService.updateOrderStatus(Mockito.any(PaymentOrderStatusVm.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
    }

    @Test
    void test_capturePayment_shouldReturnOrder() {
        RestAssured.given(getRequestSpecification())
            .body(capturedPayment)
            .post(PAYMENT_CAPTURE_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .log().ifValidationFails();
    }

    @Test
    void testCreatePaymentFromEvent_whenNormalCase_shouldReturnLong() {
        RestAssured.given(getRequestSpecification())
            .body(checkoutPaymentRequest)
            .post(EVENT_CREATE_PAYMENT_URL)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .log().ifValidationFails();
    }
}