package com.yas.order.kafka.consumer;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.order.model.Checkout;
import com.yas.order.model.enumeration.CheckoutProgress;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.service.CheckoutService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class PaymentOrderIdUpdateConsumerTest {

    private CheckoutService checkoutService;

    private PaymentPaypalOrderIdUpdateConsumer paymentUpdateConsumer;

    private final String jsonRecord = "{\"op\":\"u\"," +
        " \"before\":{\"payment_provider_checkout_id\":\"OLD\"}," +
        " \"after\":{\"payment_provider_checkout_id\":\"NEW\"," +
        " \"id\":\"1\"," +
        " \"checkout_id\":\"12345\"}}";

    @BeforeEach
    void setUp() {
        checkoutService = mock(CheckoutService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        paymentUpdateConsumer = new PaymentPaypalOrderIdUpdateConsumer(checkoutService, objectMapper);
    }

    @Test
    void testListen_whenConsumerRecordIsNull_shouldLogInfoAndDoNothing() {

        paymentUpdateConsumer.listen(null);

        verify(checkoutService, never()).findCheckoutById(anyString());
        verify(checkoutService, never()).updateCheckout(any());
    }

    @Test
    void testListen_whenValueDoesNotContainOpField_shouldNotUpdateCheckout() {
        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);

        when(consumerRecord.value()).thenReturn("{\"invalidKey\":\"value\"}");

        paymentUpdateConsumer.listen(consumerRecord);

        verify(checkoutService, never()).findCheckoutById(anyString());
        verify(checkoutService, never()).updateCheckout(any());
    }

    @Test
    void testListen_whenEventDoesNotContainBeforeField_shouldNotUpdateCheckout() {
        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("{\"op\":\"u\"," +
            " \"after\":{\"payment_provider_checkout_id\":\"OLD\"}}");

        paymentUpdateConsumer.listen(consumerRecord);

        verify(checkoutService, never()).findCheckoutById(anyString());
        verify(checkoutService, never()).updateCheckout(any());
    }

    @Test
    void testListen_whenEventDoesNotContainAfterField_shouldNotUpdateCheckout() {
        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("{\"op\":\"u\"," +
            " \"before\":{\"payment_provider_checkout_id\":\"OLD\"}}");

        paymentUpdateConsumer.listen(consumerRecord);

        verify(checkoutService, never()).updateCheckout(any());
        verify(checkoutService, never()).findCheckoutById(anyString());
    }

    @Test
    void testListen_whenEventAfterPaypalOrderIdIsNull_shouldNotUpdateCheckout() {
        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("{\"op\":\"u\"," +
            " \"before\":{\"payment_provider_checkout_id\":\"OLD\"}," +
            " \"after\":{" +
            " \"id\":\"1\"," +
            " \"checkout_id\":\"12345\"}}");

        paymentUpdateConsumer.listen(consumerRecord);

        verify(checkoutService, never()).updateCheckout(any());
    }

    @Test
    void testListen_whenEventAfterPaypalOrderIdEqualsBefore_shouldNotUpdateCheckout() {
        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("{\"op\":\"u\"," +
            " \"before\":{\"payment_provider_checkout_id\":\"OLD\"}," +
            " \"after\":{\"payment_provider_checkout_id\":\"OLD\"," +
            " \"id\":\"1\"," +
            " \"checkout_id\":\"12345\"}}");

        paymentUpdateConsumer.listen(consumerRecord);

        verify(checkoutService, never()).updateCheckout(any());
        verify(checkoutService, never()).findCheckoutById(anyString());
    }

    @Test
    void testListen_whenEventIsUpdateAndAttributesChanged_shouldUpdateCheckout() {

        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn(jsonRecord);

        Checkout mockCheckout = new Checkout();
        mockCheckout.setId("12345");
        when(checkoutService.findCheckoutById("12345")).thenReturn(mockCheckout);

        paymentUpdateConsumer.listen(consumerRecord);

        ArgumentCaptor<Checkout> checkoutCaptor = ArgumentCaptor.forClass(Checkout.class);
        verify(checkoutService).updateCheckout(checkoutCaptor.capture());

        Checkout updatedCheckout = checkoutCaptor.getValue();
        assertEquals(CheckoutState.PAYMENT_PROCESSING, updatedCheckout.getCheckoutState());
        assertEquals(CheckoutProgress.PAYMENT_CREATED, updatedCheckout.getProgress());
    }
}
