package com.yas.payment.service.provider.handler;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.paypal.service.PaypalService;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentResponse;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentResponse;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaypalHandlerTest {

    @Mock
    private PaymentProviderService paymentProviderService;

    @Mock
    private PaypalService paypalService;

    @InjectMocks
    private PaypalHandler paypalHandler;

    private InitPaymentRequestVm initPaymentRequest;
    private CapturePaymentRequestVm capturePaymentRequest;
    private PaypalCreatePaymentResponse paypalCreateResponse;
    private PaypalCapturePaymentResponse paypalCaptureResponse;

    @BeforeEach
    void setUp() {
        initPaymentRequest = InitPaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .totalPrice(new BigDecimal("99.99"))
                .checkoutId("checkout-123")
                .build();

        capturePaymentRequest = CapturePaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .token("payment-token-456")
                .build();

        paypalCreateResponse = PaypalCreatePaymentResponse.builder()
                .status("CREATED")
                .paymentId("pay_123456")
                .redirectUrl("https://sandbox.paypal.com/checkout/123")
                .build();

        paypalCaptureResponse = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout-123")
                .amount(new BigDecimal("99.99"))
                .paymentFee(new BigDecimal("2.99"))
                .gatewayTransactionId("txn_789012")
                .paymentMethod("PAYPAL")
                .paymentStatus("COMPLETED")
                .failureMessage(null)
                .build();
    }

    @Test
    void testGetProviderId_ShouldReturnPaypal() {
        // When
        String providerId = paypalHandler.getProviderId();

        // Then
        assertThat(providerId).isEqualTo("PAYPAL");
    }

    @Test
    void testInitPayment_Success() {
        // Given
        when(paypalService.createPayment(any(PaypalCreatePaymentRequest.class)))
                .thenReturn(paypalCreateResponse);

        // When
        InitiatedPayment result = paypalHandler.initPayment(initPaymentRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("CREATED");
        assertThat(result.getPaymentId()).isEqualTo("pay_123456");
        assertThat(result.getRedirectUrl()).isEqualTo("https://sandbox.paypal.com/checkout/123");
    }

    @Test
    void testInitPayment_ShouldCallPaypalServiceWithCorrectRequest() {
        // Given
        ArgumentCaptor<PaypalCreatePaymentRequest> requestCaptor = 
            ArgumentCaptor.forClass(PaypalCreatePaymentRequest.class);
        
        when(paypalService.createPayment(any(PaypalCreatePaymentRequest.class)))
                .thenReturn(paypalCreateResponse);

        // When
        paypalHandler.initPayment(initPaymentRequest);

        // Then
        verify(paypalService).createPayment(requestCaptor.capture());
        PaypalCreatePaymentRequest capturedRequest = requestCaptor.getValue();
        
        assertThat(capturedRequest.totalPrice()).isEqualTo(new BigDecimal("99.99"));
        assertThat(capturedRequest.checkoutId()).isEqualTo("checkout-123");
        assertThat(capturedRequest.paymentMethod()).isEqualTo("PAYPAL");
    }

    @Test
    void testInitPayment_WithDifferentAmount() {
        // Given
        InitPaymentRequestVm differentAmountRequest = InitPaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .totalPrice(new BigDecimal("250.00"))
                .checkoutId("checkout-250")
                .build();

        PaypalCreatePaymentResponse differentResponse = PaypalCreatePaymentResponse.builder()
                .status("CREATED")
                .paymentId("pay_250")
                .redirectUrl("https://sandbox.paypal.com/checkout/250")
                .build();

        when(paypalService.createPayment(any(PaypalCreatePaymentRequest.class)))
                .thenReturn(differentResponse);

        // When
        InitiatedPayment result = paypalHandler.initPayment(differentAmountRequest);

        // Then
        assertThat(result.getPaymentId()).isEqualTo("pay_250");
    }

    @Test
    void testCapturePayment_Success() {
        // Given
        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class)))
                .thenReturn(paypalCaptureResponse);

        // When
        CapturedPayment result = paypalHandler.capturePayment(capturePaymentRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCheckoutId()).isEqualTo("checkout-123");
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("99.99"));
        assertThat(result.getPaymentFee()).isEqualTo(new BigDecimal("2.99"));
        assertThat(result.getGatewayTransactionId()).isEqualTo("txn_789012");
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.PAYPAL);
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(result.getFailureMessage()).isNull();
    }

    @Test
    void testCapturePayment_ShouldCallPaypalServiceWithCorrectRequest() {
        // Given
        ArgumentCaptor<PaypalCapturePaymentRequest> requestCaptor = 
            ArgumentCaptor.forClass(PaypalCapturePaymentRequest.class);
        
        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class)))
                .thenReturn(paypalCaptureResponse);

        // When
        paypalHandler.capturePayment(capturePaymentRequest);

        // Then
        verify(paypalService).capturePayment(requestCaptor.capture());
        PaypalCapturePaymentRequest capturedRequest = requestCaptor.getValue();
        
        assertThat(capturedRequest.token()).isEqualTo("payment-token-456");
    }

    @Test
    void testCapturePayment_WithPendingStatus() {
        // Given
        PaypalCapturePaymentResponse pendingResponse = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout-pending")
                .amount(new BigDecimal("50.00"))
                .paymentFee(new BigDecimal("1.00"))
                .gatewayTransactionId("txn_pending_001")
                .paymentMethod("PAYPAL")
                .paymentStatus("PENDING")
                .failureMessage(null)
                .build();

        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class)))
                .thenReturn(pendingResponse);

        // When
        CapturedPayment result = paypalHandler.capturePayment(capturePaymentRequest);

        // Then
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(result.getFailureMessage()).isNull();
    }

    @Test
    void testCapturePayment_WithFailure() {
        // Given
        PaypalCapturePaymentResponse failedResponse = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout-failed")
                .amount(new BigDecimal("200.00"))
                .paymentFee(new BigDecimal("5.00"))
                .gatewayTransactionId(null)
                .paymentMethod("PAYPAL")
                .paymentStatus("CANCELLED")
                .failureMessage("Insufficient funds")
                .build();

        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class)))
                .thenReturn(failedResponse);

        // When
        CapturedPayment result = paypalHandler.capturePayment(capturePaymentRequest);

        // Then
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
        assertThat(result.getFailureMessage()).isEqualTo("Insufficient funds");
        assertThat(result.getGatewayTransactionId()).isNull();
    }

    @Test
    void testCapturePayment_WithZeroPaymentFee() {
        // Given
        PaypalCapturePaymentResponse zeroFeeResponse = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout-zero-fee")
                .amount(new BigDecimal("75.00"))
                .paymentFee(BigDecimal.ZERO)
                .gatewayTransactionId("txn_zero_001")
                .paymentMethod("PAYPAL")
                .paymentStatus("COMPLETED")
                .failureMessage(null)
                .build();

        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class)))
                .thenReturn(zeroFeeResponse);

        // When
        CapturedPayment result = paypalHandler.capturePayment(capturePaymentRequest);

        // Then
        assertThat(result.getPaymentFee()).isEqualTo(BigDecimal.ZERO);
    }
}