package com.yas.payment.service.provider.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.yas.payment.model.enumeration.PaymentMethod;
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
    void getProviderId_ReturnsPaypal() {
        assertThat(paypalHandler.getProviderId()).isEqualTo(PaymentMethod.PAYPAL.name());
    }

    @Test
    void initPayment_Success() {
        InitPaymentRequestVm request = InitPaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .totalPrice(BigDecimal.TEN)
                .checkoutId("checkout-123")
                .build();
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(eq(PaymentMethod.PAYPAL.name())))
                .thenReturn("{\"clientId\":\"test\"}");
        when(paypalService.createPayment(any(PaypalCreatePaymentRequest.class)))
                .thenReturn(PaypalCreatePaymentResponse.builder()
                        .status("APPROVED")
                        .paymentId("pay-123")
                        .redirectUrl("https://paypal.com/checkout")
                        .build());

        var result = paypalHandler.initPayment(request);

        assertThat(result.getPaymentId()).isEqualTo("pay-123");
        assertThat(result.getStatus()).isEqualTo("APPROVED");
        assertThat(result.getRedirectUrl()).isEqualTo("https://paypal.com/checkout");
    }

    @Test
    void capturePayment_Success() {
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .token("token-123")
                .build();
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(eq(PaymentMethod.PAYPAL.name())))
                .thenReturn("{\"clientId\":\"test\"}");
        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class)))
                .thenReturn(PaypalCapturePaymentResponse.builder()
                        .checkoutId("checkout-123")
                        .amount(BigDecimal.valueOf(100))
                        .paymentFee(BigDecimal.ONE)
                        .gatewayTransactionId("txn-123")
                        .paymentMethod(PaymentMethod.PAYPAL.name())
                        .paymentStatus("COMPLETED")
                        .failureMessage(null)
                        .build());

        var result = paypalHandler.capturePayment(request);

        assertThat(result.getCheckoutId()).isEqualTo("checkout-123");
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(result.getPaymentStatus().name()).isEqualTo("COMPLETED");
    }
}
