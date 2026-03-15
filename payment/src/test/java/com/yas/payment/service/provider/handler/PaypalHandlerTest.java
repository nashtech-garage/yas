package com.yas.payment.service.provider.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.paypal.service.PaypalService;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentResponse;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentResponse;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaypalHandlerTest {

    private PaypalService paypalService;
    private PaymentProviderService paymentProviderService;
    private PaypalHandler paypalHandler;

    @BeforeEach
    void setUp() {
        paypalService = mock(PaypalService.class);
        paymentProviderService = mock(PaymentProviderService.class);
        paypalHandler = new PaypalHandler(paymentProviderService, paypalService);
    }

    @Test
    void testGetProviderId_shouldReturnPaypal() {
        assertEquals(PaymentMethod.PAYPAL.name(), paypalHandler.getProviderId());
    }

    @Test
    void testInitPayment_shouldReturnInitiatedPayment() {
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(any()))
            .thenReturn("settings");
        PaypalCreatePaymentResponse mockResponse = PaypalCreatePaymentResponse.builder()
            .status("CREATED")
            .paymentId("PAY-123")
            .redirectUrl("http://redirect.url")
            .build();
        when(paypalService.createPayment(any())).thenReturn(mockResponse);

        // record: (paymentMethod, totalPrice, checkoutId)
        InitPaymentRequestVm request = new InitPaymentRequestVm(
            PaymentMethod.PAYPAL.name(), BigDecimal.TEN, "checkout-1");
        InitiatedPayment result = paypalHandler.initPayment(request);

        assertNotNull(result);
        assertEquals("CREATED", result.getStatus());
        assertEquals("PAY-123", result.getPaymentId());
        assertEquals("http://redirect.url", result.getRedirectUrl());
    }

    @Test
    void testCapturePayment_shouldReturnCapturedPayment() {
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(any()))
            .thenReturn("settings");
        PaypalCapturePaymentResponse mockResponse = PaypalCapturePaymentResponse.builder()
            .checkoutId("checkout-1")
            .amount(BigDecimal.TEN)
            .paymentFee(BigDecimal.ONE)
            .gatewayTransactionId("TXN-456")
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .paymentStatus(PaymentStatus.COMPLETED.name())
            .failureMessage(null)
            .build();
        when(paypalService.capturePayment(any())).thenReturn(mockResponse);

        // record: (paymentMethod, token)
        CapturePaymentRequestVm request = new CapturePaymentRequestVm(
            PaymentMethod.PAYPAL.name(), "token-abc");
        CapturedPayment result = paypalHandler.capturePayment(request);

        assertNotNull(result);
        assertEquals("checkout-1", result.getCheckoutId());
        assertEquals(BigDecimal.TEN, result.getAmount());
        assertEquals(PaymentMethod.PAYPAL, result.getPaymentMethod());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
    }
}
