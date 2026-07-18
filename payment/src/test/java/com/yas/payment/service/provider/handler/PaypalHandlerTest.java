package com.yas.payment.service.provider.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.paypal.service.PaypalService;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentResponse;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentResponse;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaypalHandlerTest {

    @Mock
    private PaymentProviderService paymentProviderService;

    @Mock
    private PaypalService paypalService;

    private PaypalHandler paypalHandler;

    @BeforeEach
    void setUp() {
        paypalHandler = new PaypalHandler(paymentProviderService, paypalService);
    }

    @Test
    void getProviderId_shouldReturnPaypal() {
        assertEquals(PaymentMethod.PAYPAL.name(), paypalHandler.getProviderId());
    }

    @Test
    void initPayment_shouldReturnInitiatedPayment() {
        InitPaymentRequestVm requestVm = new InitPaymentRequestVm("checkout1", BigDecimal.TEN, "PAYPAL");
        PaypalCreatePaymentResponse response = new PaypalCreatePaymentResponse("CREATED", "payment1", "http://redirect");

        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(any())).thenReturn("settings");
        when(paypalService.createPayment(any())).thenReturn(response);

        InitiatedPayment result = paypalHandler.initPayment(requestVm);

        assertEquals("CREATED", result.getStatus());
        assertEquals("payment1", result.getPaymentId());
        assertEquals("http://redirect", result.getRedirectUrl());
    }

    @Test
    void capturePayment_shouldReturnCapturedPayment() {
        CapturePaymentRequestVm requestVm = new CapturePaymentRequestVm("token1", "PAYPAL");
        PaypalCapturePaymentResponse response = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout1")
                .amount(BigDecimal.TEN)
                .paymentFee(BigDecimal.ONE)
                .gatewayTransactionId("trans1")
                .paymentMethod("PAYPAL")
                .paymentStatus("COMPLETED")
                .failureMessage(null)
                .build();

        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(any())).thenReturn("settings");
        when(paypalService.capturePayment(any())).thenReturn(response);

        CapturedPayment result = paypalHandler.capturePayment(requestVm);

        assertEquals("checkout1", result.getCheckoutId());
        assertEquals(BigDecimal.TEN, result.getAmount());
        assertEquals(BigDecimal.ONE, result.getPaymentFee());
        assertEquals("trans1", result.getGatewayTransactionId());
        assertEquals(PaymentMethod.PAYPAL, result.getPaymentMethod());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
    }

    @Test
    void capturePayment_whenPaypalOrderNotApproved_returnsCancelledInsteadOfThrowing() {
        // Cancelled/never-approved PayPal orders only come back with a failureMessage --
        // paymentStatus and paymentMethod are null, which used to make
        // PaymentStatus.valueOf(null)/PaymentMethod.valueOf(null) throw an NPE.
        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class)))
            .thenReturn(PaypalCapturePaymentResponse.builder()
                .failureMessage("{\"details\":[{\"issue\":\"ORDER_NOT_APPROVED\"}]}")
                .build());

        CapturedPayment result = assertDoesNotThrow(
            () -> paypalHandler.capturePayment(new CapturePaymentRequestVm("PAYPAL", "TOKEN123")));

        assertEquals(PaymentStatus.CANCELLED, result.getPaymentStatus());
        assertEquals(PaymentMethod.PAYPAL, result.getPaymentMethod());
    }
}
