package com.yas.payment.controller;

import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private InitPaymentRequestVm initPaymentRequest;
    private InitPaymentResponseVm initPaymentResponse;
    private CapturePaymentRequestVm capturePaymentRequest;
    private CapturePaymentResponseVm capturePaymentResponse;

    @BeforeEach
    void setUp() {
        initPaymentRequest = InitPaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .totalPrice(BigDecimal.valueOf(100.00))
                .checkoutId("checkout123")
                .build();

        initPaymentResponse = InitPaymentResponseVm.builder()
                .status("SUCCESS")
                .paymentId("pay123")
                .redirectUrl("https://example.com/payment")
                .build();

        capturePaymentRequest = CapturePaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .token("token123")
                .build();

        capturePaymentResponse = CapturePaymentResponseVm.builder()
                .orderId(100L)
                .checkoutId("checkout123")
                .amount(BigDecimal.valueOf(100.00))
                .paymentFee(BigDecimal.valueOf(1.00))
                .gatewayTransactionId("txn123")
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.COMPLETED)
                .failureMessage(null)
                .build();
    }

    @Test
    void testInitPayment_success() {
        // Given
        when(paymentService.initPayment(any(InitPaymentRequestVm.class)))
                .thenReturn(initPaymentResponse);

        // When
        InitPaymentResponseVm result = paymentController.initPayment(initPaymentRequest);

        // Then
        assertNotNull(result);
        assertEquals("SUCCESS", result.status());
        assertEquals("pay123", result.paymentId());
        assertEquals("https://example.com/payment", result.redirectUrl());
        verify(paymentService, times(1)).initPayment(any(InitPaymentRequestVm.class));
    }

    @Test
    void testCapturePayment_success() {
        // Given
        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(capturePaymentResponse);

        // When
        CapturePaymentResponseVm result = paymentController.capturePayment(capturePaymentRequest);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.orderId());
        assertEquals("checkout123", result.checkoutId());
        assertEquals(PaymentStatus.COMPLETED, result.paymentStatus());
        assertNull(result.failureMessage());
        verify(paymentService, times(1)).capturePayment(any(CapturePaymentRequestVm.class));
    }

    @Test
    void testCancelPayment_success() {
        // When
        ResponseEntity<String> result = paymentController.cancelPayment();

        // Then
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Payment cancelled", result.getBody());
    }

    @Test
    void testInitPayment_withNullResponse() {
        // Given
        when(paymentService.initPayment(any(InitPaymentRequestVm.class)))
                .thenReturn(null);

        // When
        InitPaymentResponseVm result = paymentController.initPayment(initPaymentRequest);

        // Then
        assertNull(result);
        verify(paymentService, times(1)).initPayment(any(InitPaymentRequestVm.class));
    }

    @Test
    void testCapturePayment_withFailedStatus() {
        // Given
        CapturePaymentResponseVm failedResponse = CapturePaymentResponseVm.builder()
                .orderId(200L)
                .checkoutId("checkout456")
                .amount(BigDecimal.valueOf(50.00))
                .paymentFee(BigDecimal.ZERO)
                .gatewayTransactionId("txn456")
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.CANCELLED)
                .failureMessage("Payment failed")
                .build();

        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(failedResponse);

        // When
        CapturePaymentResponseVm result = paymentController.capturePayment(capturePaymentRequest);

        // Then
        assertNotNull(result);
        assertEquals(PaymentStatus.CANCELLED, result.paymentStatus());
        assertEquals("Payment failed", result.failureMessage());
        verify(paymentService, times(1)).capturePayment(any(CapturePaymentRequestVm.class));
    }
}
