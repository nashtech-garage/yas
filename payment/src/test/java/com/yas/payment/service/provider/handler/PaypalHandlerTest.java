package com.yas.payment.service.provider.handler;

import static org.assertj.core.api.Assertions.assertThat;
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
    void getProviderId_ShouldReturnPaypal() {
        assertThat(paypalHandler.getProviderId()).isEqualTo(PaymentMethod.PAYPAL.name());
    }

    @Test
    void initPayment_ShouldReturnInitiatedPayment() {
        InitPaymentRequestVm request = InitPaymentRequestVm.builder()
            .totalPrice(BigDecimal.TEN)
            .checkoutId("checkout123")
            .paymentMethod("PAYPAL")
            .build();

        PaypalCreatePaymentResponse paypalResponse = PaypalCreatePaymentResponse.builder()
            .status("CREATED")
            .paymentId("payId123")
            .redirectUrl("http://paypal.com/approve")
            .build();

        when(paypalService.createPayment(any())).thenReturn(paypalResponse);
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(any())).thenReturn("{}");

        InitiatedPayment result = paypalHandler.initPayment(request);

        assertThat(result.getStatus()).isEqualTo("CREATED");
        assertThat(result.getPaymentId()).isEqualTo("payId123");
        assertThat(result.getRedirectUrl()).isEqualTo("http://paypal.com/approve");
    }

    @Test
    void capturePayment_ShouldReturnCapturedPayment() {
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
            .token("token123")
            .build();

        PaypalCapturePaymentResponse paypalResponse = PaypalCapturePaymentResponse.builder()
            .checkoutId("checkout123")
            .amount(BigDecimal.TEN)
            .paymentFee(BigDecimal.ONE)
            .gatewayTransactionId("txn123")
            .paymentMethod("PAYPAL")
            .paymentStatus("COMPLETED")
            .failureMessage(null)
            .build();

        when(paypalService.capturePayment(any())).thenReturn(paypalResponse);
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(any())).thenReturn("{}");

        CapturedPayment result = paypalHandler.capturePayment(request);

        assertThat(result.getCheckoutId()).isEqualTo("checkout123");
        assertThat(result.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(result.getPaymentFee()).isEqualTo(BigDecimal.ONE);
        assertThat(result.getGatewayTransactionId()).isEqualTo("txn123");
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.PAYPAL);
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }
}
