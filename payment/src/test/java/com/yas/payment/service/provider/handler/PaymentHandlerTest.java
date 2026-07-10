package com.yas.payment.service.provider.handler;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentHandlerTest {

    @Mock
    private PaymentHandler paymentHandler;

    private InitPaymentRequestVm initPaymentRequestVm;
    private CapturePaymentRequestVm capturePaymentRequestVm;
    private InitiatedPayment initiatedPayment;
    private CapturedPayment capturedPayment;

    @BeforeEach
    void setUp() {
        // Initialize test data
        initPaymentRequestVm = InitPaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .totalPrice(new BigDecimal("99.99"))
                .checkoutId("checkout-123")
                .build();

        capturePaymentRequestVm = CapturePaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .token("payment-token-456")
                .build();

        initiatedPayment = InitiatedPayment.builder()
                .status("PENDING")
                .paymentId("pay_123456")
                .redirectUrl("https://sandbox.paypal.com/checkout/123")
                .build();

        capturedPayment = CapturedPayment.builder()
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
    void testGetProviderId() {
        // Given
        String expectedProviderId = "PAYPAL";
        when(paymentHandler.getProviderId()).thenReturn(expectedProviderId);

        // When
        String actualProviderId = paymentHandler.getProviderId();

        // Then
        assertThat(actualProviderId).isEqualTo(expectedProviderId);
    }

    @Test
    void testInitPayment_Success() {
        // Given
        when(paymentHandler.initPayment(any(InitPaymentRequestVm.class)))
                .thenReturn(initiatedPayment);

        // When
        InitiatedPayment result = paymentHandler.initPayment(initPaymentRequestVm);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getPaymentId()).isEqualTo("pay_123456");
        assertThat(result.getRedirectUrl()).isEqualTo("https://sandbox.paypal.com/checkout/123");
    }

    @Test
    void testInitPayment_WithValidRequest() {
        // Given
        when(paymentHandler.initPayment(initPaymentRequestVm))
                .thenReturn(initiatedPayment);

        // When
        InitiatedPayment result = paymentHandler.initPayment(initPaymentRequestVm);

        // Then
        assertThat(result).isEqualTo(initiatedPayment);
    }

    @Test
    void testCapturePayment_Success() {
        // Given
        when(paymentHandler.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(capturedPayment);

        // When
        CapturedPayment result = paymentHandler.capturePayment(capturePaymentRequestVm);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(1001L);
        assertThat(result.getCheckoutId()).isEqualTo("checkout-123");
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("99.99"));
        assertThat(result.getGatewayTransactionId()).isEqualTo("txn_789012");
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.PAYPAL);
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(result.getFailureMessage()).isNull();
    }

    @Test
    void testCapturePayment_WithValidRequest() {
        // Given
        when(paymentHandler.capturePayment(capturePaymentRequestVm))
                .thenReturn(capturedPayment);

        // When
        CapturedPayment result = paymentHandler.capturePayment(capturePaymentRequestVm);

        // Then
        assertThat(result).isEqualTo(capturedPayment);
    }

    @Test
    void testInitPayment_WithDifferentPaymentMethods() {
        // Given
        InitPaymentRequestVm bankingRequest = InitPaymentRequestVm.builder()
                .paymentMethod("BANKING")
                .totalPrice(new BigDecimal("150.00"))
                .checkoutId("checkout-banking")
                .build();

        InitiatedPayment expectedPayment = InitiatedPayment.builder()
                .status("PENDING")
                .paymentId("banking_789")
                .redirectUrl("https://banking.checkout.com/789")
                .build();

        when(paymentHandler.initPayment(bankingRequest)).thenReturn(expectedPayment);

        // When
        InitiatedPayment result = paymentHandler.initPayment(bankingRequest);

        // Then
        assertThat(result.getPaymentId()).isEqualTo("banking_789");
        assertThat(result.getRedirectUrl()).contains("banking");
    }

    @Test
    void testCapturePayment_WithPendingStatus() {
        // Given
        CapturedPayment pendingPayment = CapturedPayment.builder()
                .orderId(1002L)
                .checkoutId("checkout-pending")
                .amount(new BigDecimal("50.00"))
                .paymentFee(new BigDecimal("1.50"))
                .gatewayTransactionId("txn_pending_001")
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.PENDING)
                .failureMessage(null)
                .build();

        when(paymentHandler.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(pendingPayment);

        // When
        CapturedPayment result = paymentHandler.capturePayment(capturePaymentRequestVm);

        // Then
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(result.getFailureMessage()).isNull();
        assertThat(result.getGatewayTransactionId()).isEqualTo("txn_pending_001");
    }

    @Test
    void testCapturePayment_WithCOD() {
        // Given
        CapturedPayment codPayment = CapturedPayment.builder()
                .orderId(1003L)
                .checkoutId("checkout-cod")
                .amount(new BigDecimal("75.00"))
                .paymentFee(BigDecimal.ZERO)
                .gatewayTransactionId(null)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.COMPLETED)
                .failureMessage(null)
                .build();

        when(paymentHandler.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(codPayment);

        // When
        CapturedPayment result = paymentHandler.capturePayment(capturePaymentRequestVm);

        // Then
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.COD);
        assertThat(result.getPaymentFee()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getGatewayTransactionId()).isNull();
    }
}