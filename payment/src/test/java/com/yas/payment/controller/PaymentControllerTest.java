package com.yas.payment.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class PaymentControllerTest {

    private PaymentService paymentService;
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        paymentController = new PaymentController(paymentService);
    }

    @Test
    @DisplayName("initPayment - should delegate to service and return response")
    void initPayment_ShouldReturnInitPaymentResponse_WhenValidRequest() {
        // Given
        InitPaymentRequestVm requestVm = InitPaymentRequestVm.builder()
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .totalPrice(BigDecimal.valueOf(100.0))
            .checkoutId("checkout-123")
            .build();

        InitPaymentResponseVm expectedResponse = InitPaymentResponseVm.builder()
            .paymentId("pay-abc")
            .status("CREATED")
            .redirectUrl("https://paypal.com/redirect")
            .build();

        when(paymentService.initPayment(any(InitPaymentRequestVm.class))).thenReturn(expectedResponse);

        // When
        InitPaymentResponseVm result = paymentController.initPayment(requestVm);

        // Then
        assertEquals(expectedResponse.paymentId(), result.paymentId());
        assertEquals(expectedResponse.status(), result.status());
        assertEquals(expectedResponse.redirectUrl(), result.redirectUrl());
        verify(paymentService, times(1)).initPayment(requestVm);
    }

    @Test
    @DisplayName("capturePayment - should delegate to service and return response")
    void capturePayment_ShouldReturnCapturePaymentResponse_WhenValidRequest() {
        // Given
        CapturePaymentRequestVm requestVm = CapturePaymentRequestVm.builder()
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .token("token-xyz")
            .build();

        CapturePaymentResponseVm expectedResponse = CapturePaymentResponseVm.builder()
            .orderId(1L)
            .checkoutId("checkout-123")
            .amount(BigDecimal.valueOf(100.0))
            .paymentFee(BigDecimal.valueOf(2.5))
            .gatewayTransactionId("txn-001")
            .paymentMethod(PaymentMethod.PAYPAL)
            .paymentStatus(PaymentStatus.COMPLETED)
            .failureMessage(null)
            .build();

        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class))).thenReturn(expectedResponse);

        // When
        CapturePaymentResponseVm result = paymentController.capturePayment(requestVm);

        // Then
        assertEquals(expectedResponse.orderId(), result.orderId());
        assertEquals(expectedResponse.checkoutId(), result.checkoutId());
        assertEquals(expectedResponse.amount(), result.amount());
        assertEquals(expectedResponse.paymentStatus(), result.paymentStatus());
        verify(paymentService, times(1)).capturePayment(requestVm);
    }

    @Test
    @DisplayName("cancelPayment - should return 200 OK with cancellation message")
    void cancelPayment_ShouldReturn200_WithCancellationMessage() {
        // When
        ResponseEntity<String> result = paymentController.cancelPayment();

        // Then
        assertEquals(200, result.getStatusCode().value());
        assertEquals("Payment cancelled", result.getBody());
    }
}
