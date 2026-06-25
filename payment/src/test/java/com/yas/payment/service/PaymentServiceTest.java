package com.yas.payment.service;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.repository.PaymentRepository;
import com.yas.payment.service.provider.handler.PaymentHandler;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import com.yas.payment.viewmodel.PaymentOrderStatusVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentHandler paymentHandler;

    private PaymentService paymentService;

    @Captor
    private ArgumentCaptor<Payment> paymentCaptor;

    @Captor
    private ArgumentCaptor<PaymentOrderStatusVm> paymentOrderStatusVmCaptor;

    @BeforeEach
    void setUp() {
        when(paymentHandler.getProviderId()).thenReturn("PAYPAL");
        paymentService = new PaymentService(paymentRepository, orderService, List.of(paymentHandler));
        paymentService.initializeProviders();
    }

    @Test
    void initPayment_whenProviderExists_returnInitPaymentResponse() {
        InitPaymentRequestVm request = new InitPaymentRequestVm("PAYPAL", BigDecimal.TEN, "checkout123");
        InitiatedPayment initiatedPayment = new InitiatedPayment(PaymentStatus.PENDING.name(), "pay123", "http://redirect.url");

        when(paymentHandler.initPayment(request)).thenReturn(initiatedPayment);

        InitPaymentResponseVm response = paymentService.initPayment(request);

        assertThat(response.paymentId()).isEqualTo("pay123");
        assertThat(response.redirectUrl()).isEqualTo("http://redirect.url");
        assertThat(response.status()).isEqualTo(PaymentStatus.PENDING.name());
    }

    @Test
    void initPayment_whenProviderDoesNotExist_throwIllegalArgumentException() {
        InitPaymentRequestVm request = new InitPaymentRequestVm("STRIPE", BigDecimal.TEN, "checkout123");

        assertThrows(IllegalArgumentException.class, () -> paymentService.initPayment(request));
    }

    @Test
    void capturePayment_whenValidRequest_returnCapturePaymentResponse() {
        CapturePaymentRequestVm request = new CapturePaymentRequestVm("PAYPAL", "token123");
        CapturedPayment capturedPayment = new CapturedPayment(
                1L,
                "checkout123",
                BigDecimal.TEN,
                BigDecimal.ONE,
                "txn123",
                com.yas.payment.model.enumeration.PaymentMethod.PAYPAL,
                PaymentStatus.COMPLETED,
                "failure"
        );

        when(paymentHandler.capturePayment(request)).thenReturn(capturedPayment);
        when(orderService.updateCheckoutStatus(capturedPayment)).thenReturn(1L);
        
        Payment savedPayment = Payment.builder()
                .id(100L)
                .orderId(1L)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        CapturePaymentResponseVm response = paymentService.capturePayment(request);

        verify(paymentRepository).save(paymentCaptor.capture());
        Payment capturedToSave = paymentCaptor.getValue();
        assertThat(capturedToSave.getCheckoutId()).isEqualTo("checkout123");
        assertThat(capturedToSave.getOrderId()).isEqualTo(1L);
        assertThat(capturedToSave.getAmount()).isEqualTo(BigDecimal.TEN);

        verify(orderService).updateOrderStatus(paymentOrderStatusVmCaptor.capture());
        PaymentOrderStatusVm statusVm = paymentOrderStatusVmCaptor.getValue();
        assertThat(statusVm.paymentId()).isEqualTo(100L);
        assertThat(statusVm.orderId()).isEqualTo(1L);
        assertThat(statusVm.paymentStatus()).isEqualTo("COMPLETED");

        assertThat(response.checkoutId()).isEqualTo("checkout123");
        assertThat(response.orderId()).isEqualTo(1L);
        assertThat(response.amount()).isEqualTo(BigDecimal.TEN);
        assertThat(response.gatewayTransactionId()).isEqualTo("txn123");
        assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }
}
