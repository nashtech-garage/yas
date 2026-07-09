package com.yas.payment.controller;

import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
        // Initialize test data for Init Payment
        initPaymentRequest = InitPaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .totalPrice(new BigDecimal("99.99"))
                .checkoutId("checkout-123")
                .build();

        initPaymentResponse = InitPaymentResponseVm.builder()
                .status("PENDING")
                .paymentId("pay_123456")
                .redirectUrl("https://sandbox.paypal.com/checkout/123")
                .build();

        // Initialize test data for Capture Payment
        capturePaymentRequest = CapturePaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .token("payment-token-456")
                .build();

        capturePaymentResponse = CapturePaymentResponseVm.builder()
                .orderId(1001L)
                .checkoutId("checkout-123")
                .amount(new BigDecimal("99.99"))
                .paymentFee(new BigDecimal("2.99"))
                .gatewayTransactionId("txn_789012")
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.COMPLETED)
                .failureMessage(null)
                .build();
    }

    @Test
    void testInitPayment_Success() {
        // Given
        when(paymentService.initPayment(any(InitPaymentRequestVm.class)))
                .thenReturn(initPaymentResponse);

        // When
        InitPaymentResponseVm result = paymentController.initPayment(initPaymentRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo("PENDING");
        assertThat(result.paymentId()).isEqualTo("pay_123456");
        assertThat(result.redirectUrl()).isEqualTo("https://sandbox.paypal.com/checkout/123");
    }

    @Test
    void testInitPayment_ShouldReturnCorrectResponse() {
        // Given
        when(paymentService.initPayment(initPaymentRequest)).thenReturn(initPaymentResponse);

        // When
        InitPaymentResponseVm result = paymentController.initPayment(initPaymentRequest);

        // Then
        assertThat(result).isEqualTo(initPaymentResponse);
    }

    @Test
    void testInitPayment_WithBankingMethod() {
        // Given
        InitPaymentRequestVm bankingRequest = InitPaymentRequestVm.builder()
                .paymentMethod("BANKING")
                .totalPrice(new BigDecimal("150.00"))
                .checkoutId("checkout-banking")
                .build();

        InitPaymentResponseVm expectedResponse = InitPaymentResponseVm.builder()
                .status("PENDING")
                .paymentId("banking_789")
                .redirectUrl("https://banking.checkout.com/789")
                .build();

        when(paymentService.initPayment(bankingRequest)).thenReturn(expectedResponse);

        // When
        InitPaymentResponseVm result = paymentController.initPayment(bankingRequest);

        // Then
        assertThat(result.paymentId()).isEqualTo("banking_789");
        assertThat(result.status()).isEqualTo("PENDING");
    }

    @Test
    void testInitPayment_WithCODMethod() {
        // Given
        InitPaymentRequestVm codRequest = InitPaymentRequestVm.builder()
                .paymentMethod("COD")
                .totalPrice(new BigDecimal("75.50"))
                .checkoutId("checkout-cod")
                .build();

        InitPaymentResponseVm expectedResponse = InitPaymentResponseVm.builder()
                .status("PENDING")
                .paymentId("cod_001")
                .redirectUrl(null)
                .build();

        when(paymentService.initPayment(codRequest)).thenReturn(expectedResponse);

        // When
        InitPaymentResponseVm result = paymentController.initPayment(codRequest);

        // Then
        assertThat(result.paymentId()).isEqualTo("cod_001");
        assertThat(result.redirectUrl()).isNull();
    }

    @Test
    void testCapturePayment_Success() {
        // Given
        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(capturePaymentResponse);

        // When
        CapturePaymentResponseVm result = paymentController.capturePayment(capturePaymentRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.orderId()).isEqualTo(1001L);
        assertThat(result.checkoutId()).isEqualTo("checkout-123");
        assertThat(result.amount()).isEqualTo(new BigDecimal("99.99"));
        assertThat(result.paymentFee()).isEqualTo(new BigDecimal("2.99"));
        assertThat(result.gatewayTransactionId()).isEqualTo("txn_789012");
        assertThat(result.paymentMethod()).isEqualTo(PaymentMethod.PAYPAL);
        assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(result.failureMessage()).isNull();
    }

    @Test
    void testCapturePayment_ShouldReturnCorrectResponse() {
        // Given
        when(paymentService.capturePayment(capturePaymentRequest)).thenReturn(capturePaymentResponse);

        // When
        CapturePaymentResponseVm result = paymentController.capturePayment(capturePaymentRequest);

        // Then
        assertThat(result).isEqualTo(capturePaymentResponse);
    }

    @Test
    void testCapturePayment_WithBankingMethod() {
        // Given
        CapturePaymentRequestVm bankingRequest = CapturePaymentRequestVm.builder()
                .paymentMethod("BANKING")
                .token("banking-token-123")
                .build();

        CapturePaymentResponseVm expectedResponse = CapturePaymentResponseVm.builder()
                .orderId(1002L)
                .checkoutId("checkout-banking")
                .amount(new BigDecimal("150.00"))
                .paymentFee(new BigDecimal("3.00"))
                .gatewayTransactionId("txn_banking_001")
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.COMPLETED)
                .failureMessage(null)
                .build();

        when(paymentService.capturePayment(bankingRequest)).thenReturn(expectedResponse);

        // When
        CapturePaymentResponseVm result = paymentController.capturePayment(bankingRequest);

        // Then
        assertThat(result.paymentMethod()).isEqualTo(PaymentMethod.BANKING);
        assertThat(result.gatewayTransactionId()).isEqualTo("txn_banking_001");
    }

    @Test
    void testCapturePayment_WithPendingStatus() {
        // Given
        CapturePaymentResponseVm pendingResponse = CapturePaymentResponseVm.builder()
                .orderId(1003L)
                .checkoutId("checkout-pending")
                .amount(new BigDecimal("50.00"))
                .paymentFee(new BigDecimal("1.00"))
                .gatewayTransactionId("txn_pending_001")
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.PENDING)
                .failureMessage(null)
                .build();

        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(pendingResponse);

        // When
        CapturePaymentResponseVm result = paymentController.capturePayment(capturePaymentRequest);

        // Then
        assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(result.failureMessage()).isNull();
    }

    @Test
    void testCapturePayment_WithFailure() {
        // Given
        CapturePaymentResponseVm failedResponse = CapturePaymentResponseVm.builder()
                .orderId(1004L)
                .checkoutId("checkout-failed")
                .amount(new BigDecimal("200.00"))
                .paymentFee(new BigDecimal("5.00"))
                .gatewayTransactionId(null)
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.CANCELLED)
                .failureMessage("Insufficient funds")
                .build();

        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(failedResponse);

        // When
        CapturePaymentResponseVm result = paymentController.capturePayment(capturePaymentRequest);

        // Then
        assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
        assertThat(result.failureMessage()).isEqualTo("Insufficient funds");
        assertThat(result.gatewayTransactionId()).isNull();
    }

    @Test
    void testCancelPayment_ShouldReturnOkStatus() {
        // When
        ResponseEntity<String> response = paymentController.cancelPayment();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testCancelPayment_ShouldReturnSuccessMessage() {
        // When
        ResponseEntity<String> response = paymentController.cancelPayment();

        // Then
        assertThat(response.getBody()).isEqualTo("Payment cancelled");
        assertThat(response.getBody()).contains("cancelled");
    }

    @Test
    void testCancelPayment_ShouldReturnNonNullBody() {
        // When
        ResponseEntity<String> response = paymentController.cancelPayment();

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotBlank();
    }
}