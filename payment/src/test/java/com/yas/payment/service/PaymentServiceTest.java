package com.yas.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.repository.PaymentRepository;
import com.yas.payment.viewmodel.CapturedPayment;
import com.yas.payment.viewmodel.PaymentOrderStatusVm;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        payment = new Payment();
        payment.setId(1L);
        payment.setCheckoutId("secretCheckoutId");
        payment.setOrderId(2L);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentFee(BigDecimal.valueOf(500));
        payment.setPaymentMethod(PaymentMethod.BANKING);
        payment.setAmount(BigDecimal.valueOf(100.0));
        payment.setFailureMessage(null);
        payment.setGatewayTransactionId("gatewayId");
    }

    @Test
    void capturePayment_ShouldReturnUpdatedOrderPaymentStatus() {
        CapturedPayment capturedPayment = prepareCapturedPayment();
        PaymentOrderStatusVm updatedOrderStatusVm = preparePaymentOrderStatusVm(payment);

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(orderService.updateCheckoutStatus(any(CapturedPayment.class))).thenReturn(2L);
        when(orderService.updateOrderStatus(any(PaymentOrderStatusVm.class))).thenReturn(updatedOrderStatusVm);

        PaymentOrderStatusVm result = paymentService.capturePayment(capturedPayment);

        verifyPaymentCreation(capturedPayment);
        verifyOrderServiceInteractions(capturedPayment);
        verifyResult(result, payment);
    }

    @Test
    void createPayment_ShouldSaveAndReturnPayment() {
        CapturedPayment capturedPayment = prepareCapturedPayment();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.createPayment(capturedPayment);

        assertThat(result).isEqualTo(payment);
        assertThat(result.getCheckoutId()).isEqualTo(capturedPayment.checkoutId());
        assertThat(result.getOrderId()).isEqualTo(capturedPayment.orderId());
        assertThat(result.getPaymentStatus()).isEqualTo(capturedPayment.paymentStatus());
        assertThat(result.getPaymentFee()).isEqualTo(capturedPayment.paymentFee());
        assertThat(result.getAmount()).isEqualTo(capturedPayment.amount());
    }

    private CapturedPayment prepareCapturedPayment() {
        return CapturedPayment.builder()
            .orderId(2L)
            .checkoutId("secretCheckoutId")
            .amount(BigDecimal.valueOf(100.0))
            .paymentFee(BigDecimal.valueOf(500))
            .gatewayTransactionId("gatewayId")
            .paymentMethod(PaymentMethod.BANKING)
            .paymentStatus(PaymentStatus.COMPLETED)
            .failureMessage(null)
            .build();
    }

    private PaymentOrderStatusVm preparePaymentOrderStatusVm(Payment payment) {
        return PaymentOrderStatusVm.builder()
            .paymentId(payment.getId())
            .orderId(payment.getOrderId())
            .paymentStatus(payment.getPaymentStatus().name())
            .build();
    }

    private void verifyPaymentCreation(CapturedPayment capturedPayment) {
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        Payment capturedPaymentResult = paymentCaptor.getValue();

        assertThat(capturedPaymentResult.getCheckoutId()).isEqualTo(capturedPayment.checkoutId());
        assertThat(capturedPaymentResult.getOrderId()).isEqualTo(capturedPayment.orderId());
        assertThat(capturedPaymentResult.getPaymentStatus()).isEqualTo(capturedPayment.paymentStatus());
        assertThat(capturedPaymentResult.getPaymentFee()).isEqualByComparingTo(capturedPayment.paymentFee());
        assertThat(capturedPaymentResult.getAmount()).isEqualByComparingTo(capturedPayment.amount());
    }

    private void verifyOrderServiceInteractions(CapturedPayment capturedPayment) {
        verify(orderService, times(1)).updateCheckoutStatus((capturedPayment));
        verify(orderService, times(1)).updateOrderStatus(any(PaymentOrderStatusVm.class));
    }

    private void verifyResult(PaymentOrderStatusVm result, Payment payment) {
        assertThat(result.paymentId()).isEqualTo(payment.getId());
        assertThat(result.orderId()).isEqualTo(payment.getOrderId());
        assertThat(result.paymentStatus()).isEqualTo(payment.getPaymentStatus().name());
    }

}
