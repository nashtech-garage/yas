package com.yas.payment.service.provider.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class PaypalHandlerTest {

    @Test
    void getProviderId_ReturnsPaypal() {
        PaymentProviderService paymentProviderService = mock(PaymentProviderService.class);
        PaypalService paypalService = mock(PaypalService.class);
        PaypalHandler paypalHandler = new PaypalHandler(paymentProviderService, paypalService);

        assertEquals(PaymentMethod.PAYPAL.name(), paypalHandler.getProviderId());
    }

    @Test
    void initPayment_MapsRequestAndResponse() {
        PaymentProviderService paymentProviderService = mock(PaymentProviderService.class);
        PaypalService paypalService = mock(PaypalService.class);
        PaypalHandler paypalHandler = new PaypalHandler(paymentProviderService, paypalService);

        InitPaymentRequestVm requestVm = InitPaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .totalPrice(BigDecimal.valueOf(199.99))
                .checkoutId("checkout-123")
                .build();

        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name()))
                .thenReturn("paypal-settings");
        when(paypalService.createPayment(org.mockito.ArgumentMatchers.any(PaypalCreatePaymentRequest.class)))
                .thenReturn(PaypalCreatePaymentResponse.builder()
                        .status("success")
                        .paymentId("payment-001")
                        .redirectUrl("https://paypal.test/redirect")
                        .build());

        InitiatedPayment initiatedPayment = paypalHandler.initPayment(requestVm);

        ArgumentCaptor<PaypalCreatePaymentRequest> requestCaptor = ArgumentCaptor.forClass(PaypalCreatePaymentRequest.class);
        verify(paypalService).createPayment(requestCaptor.capture());
        PaypalCreatePaymentRequest capturedRequest = requestCaptor.getValue();

        assertEquals(requestVm.totalPrice(), capturedRequest.totalPrice());
        assertEquals(requestVm.checkoutId(), capturedRequest.checkoutId());
        assertEquals(requestVm.paymentMethod(), capturedRequest.paymentMethod());
        assertEquals("paypal-settings", capturedRequest.paymentSettings());

        assertEquals("success", initiatedPayment.getStatus());
        assertEquals("payment-001", initiatedPayment.getPaymentId());
        assertEquals("https://paypal.test/redirect", initiatedPayment.getRedirectUrl());
    }

    @Test
    void capturePayment_MapsRequestAndResponse() {
        PaymentProviderService paymentProviderService = mock(PaymentProviderService.class);
        PaypalService paypalService = mock(PaypalService.class);
        PaypalHandler paypalHandler = new PaypalHandler(paymentProviderService, paypalService);

        CapturePaymentRequestVm requestVm = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .token("token-xyz")
                .build();

        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name()))
                .thenReturn("paypal-settings");
        when(paypalService.capturePayment(org.mockito.ArgumentMatchers.any(PaypalCapturePaymentRequest.class)))
                .thenReturn(PaypalCapturePaymentResponse.builder()
                        .checkoutId("checkout-xyz")
                        .amount(BigDecimal.valueOf(299.99))
                        .paymentFee(BigDecimal.valueOf(3.99))
                        .gatewayTransactionId("gateway-123")
                        .paymentMethod(PaymentMethod.PAYPAL.name())
                        .paymentStatus(PaymentStatus.COMPLETED.name())
                        .failureMessage(null)
                        .build());

        CapturedPayment capturedPayment = paypalHandler.capturePayment(requestVm);

        ArgumentCaptor<PaypalCapturePaymentRequest> requestCaptor =
                ArgumentCaptor.forClass(PaypalCapturePaymentRequest.class);
        verify(paypalService).capturePayment(requestCaptor.capture());
        PaypalCapturePaymentRequest capturedRequest = requestCaptor.getValue();

        assertEquals("token-xyz", capturedRequest.token());
        assertEquals("paypal-settings", capturedRequest.paymentSettings());

        assertEquals("checkout-xyz", capturedPayment.getCheckoutId());
        assertEquals(BigDecimal.valueOf(299.99), capturedPayment.getAmount());
        assertEquals(BigDecimal.valueOf(3.99), capturedPayment.getPaymentFee());
        assertEquals("gateway-123", capturedPayment.getGatewayTransactionId());
        assertEquals(PaymentMethod.PAYPAL, capturedPayment.getPaymentMethod());
        assertEquals(PaymentStatus.COMPLETED, capturedPayment.getPaymentStatus());
        assertEquals(null, capturedPayment.getFailureMessage());
    }
}
