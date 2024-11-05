package com.yas.payment.kafka.consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import java.math.BigDecimal;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class PaymentCreateConsumerTest {

    private PaymentService paymentService;

    private PaymentCreateConsumer paymentCreateConsumer;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        Gson gson = new Gson();
        paymentCreateConsumer = new PaymentCreateConsumer(paymentService, gson);
    }

    @Test
    void testListen_whenConsumerRecordIsNull_shouldLogInfoAndDoNothing() {
        ConsumerRecord<?, ?> consumerRecord = null;

        paymentCreateConsumer.listen(consumerRecord);

        verify(paymentService, never()).findPaymentById(anyLong());
        verify(paymentService, never()).updatePayment(any());
    }

    @Test
    void testListen_whenValueDoesNotContainOpField_shouldNotProcessEvent() {
        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("{\"invalidKey\":\"value\"}");

        paymentCreateConsumer.listen(consumerRecord);

        verify(paymentService, never()).findPaymentById(anyLong());
        verify(paymentService, never()).updatePayment(any());
    }

    @Test
    void testListen_whenEventIsCreateAndPaymentMethodIsNotPaypal_shouldNotCreateOrderOnPaypal() {

        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("{\"op\":\"c\", \"after\":{\"id\":1234}}");

        Payment payment = new Payment();
        payment.setId(1234L);
        payment.setPaymentMethod(PaymentMethod.BANKING);
        payment.setAmount(new BigDecimal("100"));
        payment.setCheckoutId("CHECKOUT123");

        when(paymentService.findPaymentById(1234L)).thenReturn(payment);

        InitPaymentResponseVm responseVm = new InitPaymentResponseVm(
            "success",
            "PAYMENT123",
            "http://localhost:8080/test"
        );
        when(paymentService.initPayment(any())).thenReturn(responseVm);
        paymentCreateConsumer.listen(consumerRecord);
        verify(paymentService, never()).updatePayment(any());
    }

    @Test
    void testListen_whenEventIsCreateAndPaymentMethodIsPaypal_shouldCreateOrderOnPaypal() {
        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("{\"op\":\"c\", \"after\":{\"id\":123}}");

        Payment payment = new Payment();
        payment.setId(123L);
        payment.setPaymentMethod(PaymentMethod.PAYPAL);
        payment.setAmount(new BigDecimal("100"));
        payment.setCheckoutId("CHECKOUT123");

        when(paymentService.findPaymentById(123L)).thenReturn(payment);

        InitPaymentResponseVm responseVm = new InitPaymentResponseVm(
            "success",
            "PAYMENT123",
            "http://localhost:8080/test"
        );
        when(paymentService.initPayment(any())).thenReturn(responseVm);

        paymentCreateConsumer.listen(consumerRecord);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentService).updatePayment(paymentCaptor.capture());

        Payment updatedPayment = paymentCaptor.getValue();
        Assertions.assertEquals(PaymentStatus.PROCESSING, updatedPayment.getPaymentStatus());
        Assertions.assertEquals("PAYMENT123", updatedPayment.getPaymentProviderCheckoutId());
    }

    @Test
    void testListen_whenEventResponseIsFailed_shouldNotUpdatePayment() {

        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("{\"op\":\"c\", \"after\":{\"id\":123}}");

        Payment payment = new Payment();
        payment.setId(123L);
        payment.setPaymentMethod(PaymentMethod.PAYPAL);
        payment.setAmount(new BigDecimal("100"));
        payment.setCheckoutId("CHECKOUT123");

        when(paymentService.findPaymentById(123L)).thenReturn(payment);

        InitPaymentResponseVm responseVm = new InitPaymentResponseVm(
            "failed",
            "PAYMENT123",
            "http://localhost:8080/test"
        );
        when(paymentService.initPayment(any())).thenReturn(responseVm);

        paymentCreateConsumer.listen(consumerRecord);

        verify(paymentService, never()).updatePayment(any());
    }

    @Test
    void testListen_whenEventIsCreateAndPaymentMethodIsCOD_shouldNotCreateOrderOnPaypal() {
        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("{\"op\":\"c\", \"after\":{\"id\":123}}");

        Payment payment = new Payment();
        payment.setId(123L);
        payment.setPaymentMethod(PaymentMethod.COD);

        when(paymentService.findPaymentById(123L)).thenReturn(payment);

        paymentCreateConsumer.listen(consumerRecord);

        verify(paymentService, never()).initPayment(any());
    }

    @Test
    void testListen_whenPaymentIdDoesNotExist_shouldThrowBadRequestException() {
        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("{\"op\":\"c\", \"after\":{}}");

        assertThrows(BadRequestException.class, () -> paymentCreateConsumer.listen(consumerRecord));
    }
}
