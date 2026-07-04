package com.yas.payment.service.provider.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaypalHandlerTest {

    @Mock
    private PaymentProviderService paymentProviderService;

    @Mock
    private PaypalService paypalService;

    @InjectMocks
    private PaypalHandler paypalHandler;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getProviderId_shouldReturnPaypal() {
        assertThat(paypalHandler.getProviderId()).isEqualTo("PAYPAL");
    }

    @Test
    void initPayment_shouldReturnInitiatedPayment() {
        InitPaymentRequestVm request = new InitPaymentRequestVm("PAYPAL", BigDecimal.TEN, "checkout123");
        PaypalCreatePaymentResponse response = new PaypalCreatePaymentResponse("CREATED", "paymentId123", "http://paypal.url");

        given(paymentProviderService.getAdditionalSettingsByPaymentProviderId("PAYPAL")).willReturn("settings");
        given(paypalService.createPayment(any(PaypalCreatePaymentRequest.class))).willReturn(response);

        InitiatedPayment initiatedPayment = paypalHandler.initPayment(request);

        assertThat(initiatedPayment.getStatus()).isEqualTo("CREATED");
        assertThat(initiatedPayment.getPaymentId()).isEqualTo("paymentId123");
        assertThat(initiatedPayment.getRedirectUrl()).isEqualTo("http://paypal.url");
    }

    @Test
    void capturePayment_shouldReturnCapturedPayment() {
        CapturePaymentRequestVm request = new CapturePaymentRequestVm("PAYPAL", "token123");
        PaypalCapturePaymentResponse response = new PaypalCapturePaymentResponse(
            "checkout123", BigDecimal.TEN, BigDecimal.ONE, "gatewayId123", "PAYPAL", "COMPLETED", "failureMessage"
        );

        given(paymentProviderService.getAdditionalSettingsByPaymentProviderId("PAYPAL")).willReturn("settings");
        given(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class))).willReturn(response);

        CapturedPayment capturedPayment = paypalHandler.capturePayment(request);

        assertThat(capturedPayment.getCheckoutId()).isEqualTo("checkout123");
        assertThat(capturedPayment.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(capturedPayment.getPaymentFee()).isEqualTo(BigDecimal.ONE);
        assertThat(capturedPayment.getGatewayTransactionId()).isEqualTo("gatewayId123");
        assertThat(capturedPayment.getPaymentMethod()).isEqualTo(PaymentMethod.PAYPAL);
        assertThat(capturedPayment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(capturedPayment.getFailureMessage()).isEqualTo("failureMessage");
    }
}
