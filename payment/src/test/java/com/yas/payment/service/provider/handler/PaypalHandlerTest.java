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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PaypalHandlerTest {

    @Mock
    private PaypalService paypalService;

    @Mock
    private PaymentProviderService paymentProviderService;

    private PaypalHandler paypalHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paypalHandler = new PaypalHandler(paymentProviderService, paypalService);
    }

    @Test
    void getProviderId_ShouldReturnPaypal() {
        assertEquals(PaymentMethod.PAYPAL.name(), paypalHandler.getProviderId());
    }

    @Test
    void initPayment_ShouldReturnInitiatedPayment() {
        InitPaymentRequestVm requestVm = InitPaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .totalPrice(BigDecimal.TEN)
                .checkoutId("checkout1")
                .build();

        PaypalCreatePaymentResponse paypalResponse = PaypalCreatePaymentResponse.builder()
                .status("CREATED")
                .paymentId("pay1")
                .redirectUrl("http://redirect")
                .build();

        when(paypalService.createPayment(any(PaypalCreatePaymentRequest.class))).thenReturn(paypalResponse);
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(any())).thenReturn("{}");

        InitiatedPayment result = paypalHandler.initPayment(requestVm);

        assertEquals("CREATED", result.getStatus());
        assertEquals("pay1", result.getPaymentId());
        assertEquals("http://redirect", result.getRedirectUrl());
    }

    @Test
    void capturePayment_ShouldReturnCapturedPayment() {
        CapturePaymentRequestVm requestVm = CapturePaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .token("token1")
                .build();

        PaypalCapturePaymentResponse paypalResponse = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout1")
                .amount(BigDecimal.TEN)
                .paymentFee(BigDecimal.ONE)
                .gatewayTransactionId("txn1")
                .paymentMethod("PAYPAL")
                .paymentStatus("COMPLETED")
                .failureMessage(null)
                .build();

        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class))).thenReturn(paypalResponse);
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(any())).thenReturn("{}");

        CapturedPayment result = paypalHandler.capturePayment(requestVm);

        assertEquals("checkout1", result.getCheckoutId());
        assertEquals(BigDecimal.TEN, result.getAmount());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
    }
}
