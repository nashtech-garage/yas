package com.yas.payment.service.provider.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        assertThat(paypalHandler.getProviderId()).isEqualTo("PAYPAL");
    }

    @Test
    void initPayment_shouldCreatePaymentAndReturnInitiatedPayment() {
        // Given
        InitPaymentRequestVm requestVm = new InitPaymentRequestVm(
                "PAYPAL", BigDecimal.valueOf(100.00), "checkout-123"
        );
        String paymentSettings = "{\"clientId\":\"test\"}";
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId("PAYPAL"))
                .thenReturn(paymentSettings);

        PaypalCreatePaymentResponse response = PaypalCreatePaymentResponse.builder()
                .status("CREATED")
                .paymentId("PAY-123")
                .redirectUrl("https://paypal.com/redirect")
                .build();
        when(paypalService.createPayment(any(PaypalCreatePaymentRequest.class)))
                .thenReturn(response);

        // When
        InitiatedPayment result = paypalHandler.initPayment(requestVm);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("CREATED");
        assertThat(result.getPaymentId()).isEqualTo("PAY-123");
        assertThat(result.getRedirectUrl()).isEqualTo("https://paypal.com/redirect");
        verify(paypalService).createPayment(any(PaypalCreatePaymentRequest.class));
    }

    @Test
    void capturePayment_shouldCaptureAndReturnCapturedPayment() {
        // Given
        CapturePaymentRequestVm requestVm = new CapturePaymentRequestVm("PAYPAL", "token-abc");
        String paymentSettings = "{\"clientId\":\"test\"}";
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId("PAYPAL"))
                .thenReturn(paymentSettings);

        PaypalCapturePaymentResponse response = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout-123")
                .amount(BigDecimal.valueOf(100.00))
                .paymentFee(BigDecimal.valueOf(2.50))
                .gatewayTransactionId("TXN-456")
                .paymentMethod("PAYPAL")
                .paymentStatus("COMPLETED")
                .failureMessage(null)
                .build();
        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class)))
                .thenReturn(response);

        // When
        CapturedPayment result = paypalHandler.capturePayment(requestVm);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCheckoutId()).isEqualTo("checkout-123");
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(result.getPaymentFee()).isEqualByComparingTo(BigDecimal.valueOf(2.50));
        assertThat(result.getGatewayTransactionId()).isEqualTo("TXN-456");
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.PAYPAL);
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(result.getFailureMessage()).isNull();
        verify(paypalService).capturePayment(any(PaypalCapturePaymentRequest.class));
    }
}
