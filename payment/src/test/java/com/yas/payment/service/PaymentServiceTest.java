package com.yas.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.repository.PaymentRepository;
import com.yas.payment.service.provider.handler.PaymentHandler;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.CheckoutPaymentVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import com.yas.payment.viewmodel.PaymentOrderStatusVm;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class PaymentServiceTest {
    private PaymentRepository paymentRepository;
    private OrderService orderService;
    private PaymentHandler paymentHandler;
    private List<PaymentHandler> paymentHandlers = new ArrayList<>();
    private PaymentService paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        orderService = mock(OrderService.class);
        paymentHandler = mock(PaymentHandler.class);
        paymentHandlers.add(paymentHandler);
        paymentService = new PaymentService(paymentRepository, orderService, paymentHandlers);

        when(paymentHandler.getProviderId()).thenReturn(PaymentMethod.PAYPAL.name());
        paymentService.initializeProviders();

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
    void initPayment_Success() {
        InitPaymentRequestVm initPaymentRequestVm = InitPaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name()).totalPrice(BigDecimal.TEN).checkoutId("123").build();
        InitiatedPayment initiatedPayment = InitiatedPayment.builder().paymentId("123").status("success").redirectUrl("http://abc.com").build();
        when(paymentHandler.initPayment(initPaymentRequestVm)).thenReturn(initiatedPayment);
        InitPaymentResponseVm result = paymentService.initPayment(initPaymentRequestVm);
        assertEquals(initiatedPayment.getPaymentId(), result.paymentId());
        assertEquals(initiatedPayment.getStatus(), result.status());
        assertEquals(initiatedPayment.getRedirectUrl(), result.redirectUrl());
    }

    @Test
    void capturePayment_Success() {
        CapturePaymentRequestVm capturePaymentRequestVm = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name()).token("123").build();
        CapturedPayment capturedPayment = prepareCapturedPayment();
        Long orderId = 999L;
        when(paymentHandler.capturePayment(capturePaymentRequestVm)).thenReturn(capturedPayment);
        when(orderService.updateCheckoutStatus(capturedPayment)).thenReturn(orderId);
        when(paymentRepository.save(any())).thenReturn(payment);
        CapturePaymentResponseVm capturePaymentResponseVm = paymentService.capturePayment(capturePaymentRequestVm);
        verifyPaymentCreation(capturePaymentResponseVm);
        verifyOrderServiceInteractions(capturedPayment);
        verifyResult(capturedPayment, capturePaymentResponseVm);
    }

    @Test
    void testCreatePaymentFromEvent() {

        CheckoutPaymentVm checkoutPaymentRequestDto = new CheckoutPaymentVm(
            "123",
            PaymentMethod.PAYPAL,
            new BigDecimal(12)
        );

        Payment actualPayment = Payment.builder().id(1L).paymentMethod(PaymentMethod.PAYPAL).build();
        ArgumentCaptor<Payment> argumentCaptor = ArgumentCaptor.forClass(Payment.class);
        when(paymentRepository.save(argumentCaptor.capture())).thenReturn(actualPayment);

        Long result = paymentService.createPaymentFromEvent(checkoutPaymentRequestDto);

        assertThat(result).isEqualTo(1);
        Payment captorValue = argumentCaptor.getValue();
        assertThat(captorValue.getPaymentMethod()).isEqualTo(PaymentMethod.PAYPAL);
        assertThat(captorValue.getPaymentStatus()).isEqualTo(PaymentStatus.PROCESSING);
        assertThat(captorValue.getAmount()).isEqualTo(new BigDecimal(12));

    }

    @Test
    void testFindPaymentById_whenNormalCase_methodSuccess() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        Payment actual = paymentService.findPaymentById(1L);
        assertThat(actual).isNotNull();
    }

    @Test
    void testFindPaymentById_whenNotFound_throwNotFoundException() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException foundException
            = assertThrows(NotFoundException.class, () -> paymentService.findPaymentById(1L));
        assertThat(foundException.getMessage()).isEqualTo("Payment 1 is not found");
    }

    @Test
    void testUpdatePayment_whenNormalCase_methodSuccess() {
        paymentService.updatePayment(payment);
        verify(paymentRepository, times(1)).save(any());
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

    private void verifyPaymentCreation(CapturePaymentResponseVm capturedPayment) {
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

    private void verifyResult(CapturedPayment capturedPayment, CapturePaymentResponseVm responseVm) {
        assertEquals(capturedPayment.getOrderId(), responseVm.orderId());
        assertEquals(capturedPayment.getCheckoutId(), responseVm.checkoutId());
        assertEquals(capturedPayment.getAmount(), responseVm.amount());
        assertEquals(capturedPayment.getPaymentFee(), responseVm.paymentFee());
        assertEquals(capturedPayment.getGatewayTransactionId(), responseVm.gatewayTransactionId());
        assertEquals(capturedPayment.getPaymentMethod(), responseVm.paymentMethod());
        assertEquals(capturedPayment.getPaymentStatus(), responseVm.paymentStatus());
        assertEquals(capturedPayment.getFailureMessage(), responseVm.failureMessage());
    }

}
