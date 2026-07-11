package com.yas.payment.service.provider.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaypalHandlerTest {

    private PaymentProviderService paymentProviderService;
    private PaypalService paypalService;
    private PaypalHandler paypalHandler;

    @BeforeEach
    void setUp() {
        paymentProviderService = mock(PaymentProviderService.class);
        paypalService = mock(PaypalService.class);
        paypalHandler = new PaypalHandler(paymentProviderService, paypalService);
    }

    @Test
    @DisplayName("getProviderId - should return PAYPAL")
    void getProviderId_ShouldReturnPaypal() {
        assertEquals(PaymentMethod.PAYPAL.name(), paypalHandler.getProviderId());
    }

    @Test
    @DisplayName("initPayment - should build PaypalCreatePaymentRequest and return InitiatedPayment")
    void initPayment_ShouldReturnInitiatedPayment_WhenValidRequest() {
        // Given
        String additionalSettings = "{\"clientId\":\"test\"}";
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name()))
            .thenReturn(additionalSettings);

        InitPaymentRequestVm requestVm = InitPaymentRequestVm.builder()
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .totalPrice(BigDecimal.valueOf(99.99))
            .checkoutId("checkout-001")
            .build();

        PaypalCreatePaymentResponse paypalResponse = PaypalCreatePaymentResponse.builder()
            .status("CREATED")
            .paymentId("PAY-123")
            .redirectUrl("https://paypal.com/pay/PAY-123")
            .build();

        when(paypalService.createPayment(any(PaypalCreatePaymentRequest.class))).thenReturn(paypalResponse);

        // When
        InitiatedPayment result = paypalHandler.initPayment(requestVm);

        // Then
        assertNotNull(result);
        assertEquals("CREATED", result.getStatus());
        assertEquals("PAY-123", result.getPaymentId());
        assertEquals("https://paypal.com/pay/PAY-123", result.getRedirectUrl());
        verify(paypalService, times(1)).createPayment(any(PaypalCreatePaymentRequest.class));
    }

    @Test
    @DisplayName("capturePayment - should build PaypalCapturePaymentRequest and return CapturedPayment")
    void capturePayment_ShouldReturnCapturedPayment_WhenValidRequest() {
        // Given
        String additionalSettings = "{\"clientId\":\"test\"}";
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name()))
            .thenReturn(additionalSettings);

        CapturePaymentRequestVm requestVm = CapturePaymentRequestVm.builder()
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .token("EC-TOKEN-123")
            .build();

        PaypalCapturePaymentResponse paypalCaptureResponse = PaypalCapturePaymentResponse.builder()
            .checkoutId("checkout-001")
            .amount(BigDecimal.valueOf(99.99))
            .paymentFee(BigDecimal.valueOf(2.5))
            .gatewayTransactionId("GW-TXN-999")
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .paymentStatus(PaymentStatus.COMPLETED.name())
            .failureMessage(null)
            .build();

        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class))).thenReturn(paypalCaptureResponse);

        // When
        CapturedPayment result = paypalHandler.capturePayment(requestVm);

        // Then
        assertNotNull(result);
        assertEquals("checkout-001", result.getCheckoutId());
        assertEquals(BigDecimal.valueOf(99.99), result.getAmount());
        assertEquals(BigDecimal.valueOf(2.5), result.getPaymentFee());
        assertEquals("GW-TXN-999", result.getGatewayTransactionId());
        assertEquals(PaymentMethod.PAYPAL, result.getPaymentMethod());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
        verify(paypalService, times(1)).capturePayment(any(PaypalCapturePaymentRequest.class));
    }
}
