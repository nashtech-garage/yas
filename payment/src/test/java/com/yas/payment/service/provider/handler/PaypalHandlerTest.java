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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaypalHandlerTest {

    @Mock
    private PaymentProviderService paymentProviderService;

    @Mock
    private PaypalService paypalService;

    @InjectMocks
    private PaypalHandler paypalHandler;

    @Test
    void getProviderId_shouldReturnPaypal() {
        assertThat(paypalHandler.getProviderId()).isEqualTo(PaymentMethod.PAYPAL.name());
    }

    @Test
    void initPayment_whenValidRequest_shouldReturnInitiatedPayment() {
        InitPaymentRequestVm request = new InitPaymentRequestVm("PAYPAL", BigDecimal.TEN, "checkout123");
        PaypalCreatePaymentResponse paypalResponse = PaypalCreatePaymentResponse.builder()
                .status("PENDING")
                .paymentId("pay123")
                .redirectUrl("http://redirect.url")
                .build();

        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId("PAYPAL")).thenReturn("{}");
        when(paypalService.createPayment(any(PaypalCreatePaymentRequest.class))).thenReturn(paypalResponse);

        InitiatedPayment result = paypalHandler.initPayment(request);

        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getPaymentId()).isEqualTo("pay123");
        assertThat(result.getRedirectUrl()).isEqualTo("http://redirect.url");
    }

    @Test
    void capturePayment_whenValidRequest_shouldReturnCapturedPayment() {
        CapturePaymentRequestVm request = new CapturePaymentRequestVm("PAYPAL", "token123");
        PaypalCapturePaymentResponse paypalResponse = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout123")
                .amount(BigDecimal.TEN)
                .paymentFee(BigDecimal.ONE)
                .gatewayTransactionId("txn123")
                .paymentMethod("PAYPAL")
                .paymentStatus("COMPLETED")
                .failureMessage("none")
                .build();

        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId("PAYPAL")).thenReturn("{}");
        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class))).thenReturn(paypalResponse);

        CapturedPayment result = paypalHandler.capturePayment(request);

        assertThat(result.getCheckoutId()).isEqualTo("checkout123");
        assertThat(result.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(result.getPaymentFee()).isEqualTo(BigDecimal.ONE);
        assertThat(result.getGatewayTransactionId()).isEqualTo("txn123");
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.PAYPAL);
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(result.getFailureMessage()).isEqualTo("none");
    }
}
