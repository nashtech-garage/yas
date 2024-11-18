package com.yas.order.kafka.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.order.model.Checkout;
import com.yas.order.model.enumeration.CheckoutProgress;
import com.yas.order.service.CheckoutService;
import com.yas.order.service.PaymentService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class CheckoutFulfillmentConsumerTest {

    private PaymentService paymentService;

    private CheckoutService checkoutService;

    private CheckoutFulfillmentConsumer orderStatusConsumer;

    private final String jsonRecord = "{\"op\": \"u\"," +
        "\"before\": {"
            + " \"status\": \"PAYMENT_PROCESSING\","
            + " \"progress\": \"STOCK_LOCKED\","
            + " \"id\": 12345,"
            + " \"payment_method_id\":  \"PAYPAL\","
            + " \"total_amount\": 123},"
        +"\"after\": {"
            + " \"status\": \"PAYMENT_PROCESSING\","
            + " \"progress\": \"STOCK_LOCKED\","
            + " \"id\": 12345,"
            + " \"payment_method_id\":  \"PAYPAL\","
            + " \"total_amount\": 123}"
        + "}";

    @BeforeEach
    void setUp() {
        paymentService = Mockito.mock(PaymentService.class);
        checkoutService = Mockito.mock(CheckoutService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        orderStatusConsumer = new CheckoutFulfillmentConsumer(paymentService, checkoutService, objectMapper);

    }

    @Test
    void testListen_whenConsumerRecordIsNull_shouldNotThing() {

        orderStatusConsumer.listen(null);

        verify(checkoutService, never()).findCheckoutById(any());
        verify(paymentService, never()).createPaymentFromEvent(any());
        verify(checkoutService, never()).updateCheckout(any());
    }

    @Test
    void testListen_whenHaveNoAfter_shouldNotThing() {

        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);

        when(consumerRecord.value()).thenReturn("{}");

        orderStatusConsumer.listen(consumerRecord);

        verify(checkoutService, never()).updateCheckout(any());
        verify(checkoutService, never()).findCheckoutById(any());
        verify(paymentService, never()).createPaymentFromEvent(any());
    }

    @Test
    void testListen_whenIsNotPaymentProcess_shouldNotThing() {

        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);

        String invalidDataJson = "{\"after\": {"
            + " \"status\": \"CHECKED_OUT\","
            + " \"progress\": \"STOCK_LOCKED\","
            + " \"id\": 12345,"
            + " \"payment_method_id\":  \"PAYPAL\","
            + " \"total_amount\": 123}"
            + "}";

        when(consumerRecord.value()).thenReturn(invalidDataJson);

        orderStatusConsumer.listen(consumerRecord);

        verify(checkoutService, never()).findCheckoutById(any());
        verify(checkoutService, never()).updateCheckout(any());
        verify(paymentService, never()).createPaymentFromEvent(any());
    }

    @Test
    void testListen_whenProgressIsNotStockLocked_shouldNotThing() {

        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);

        String invalidDataJson = "{\"after\": {"
            + " \"status\": \"PAYMENT_PROCESSING\","
            + " \"progress\": \"INIT\","
            + " \"id\": 12345,"
            + " \"payment_method_id\":  \"PAYPAL\","
            + " \"total_amount\": 123}"
            + "}";

        when(consumerRecord.value()).thenReturn(invalidDataJson);

        orderStatusConsumer.listen(consumerRecord);

        verify(checkoutService, never()).findCheckoutById(any());
        verify(checkoutService, never()).updateCheckout(any());
        verify(paymentService, never()).createPaymentFromEvent(any());
    }

    @Test
    void testListen_whenCreatePaymentSuccess_shouldProcessCheckoutEvent() {

        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);

        when(consumerRecord.value()).thenReturn(jsonRecord);
        JsonNode jsonObject = mock(JsonNode.class);
        when(jsonObject.has("after")).thenReturn(true);
        when(jsonObject.get("after")).thenReturn(mock(JsonNode.class));

        ArgumentCaptor<Checkout> argumentCaptor = ArgumentCaptor.forClass(Checkout.class);

        when(checkoutService.findCheckoutById(anyString())).thenReturn(new Checkout());

        when(paymentService.createPaymentFromEvent(any())).thenReturn(2L);

        orderStatusConsumer.listen(consumerRecord);

        verify(checkoutService, atLeastOnce()).findCheckoutById(any());
        verify(paymentService, atLeastOnce()).createPaymentFromEvent(any());
        verify(checkoutService, atLeastOnce()).updateCheckout(argumentCaptor.capture());

        Checkout actual = argumentCaptor.getValue();
        assertThat(actual.getProgress()).isEqualTo(CheckoutProgress.PAYMENT_CREATED);
        assertThat(actual.getAttributes()).isEqualTo("{\"payment_id\":2}");
    }

    @Test
    void testListen_whenCreatePaymentFailure_shouldThrowException() {

        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);

        when(consumerRecord.value()).thenReturn(jsonRecord);
        JsonNode jsonObject = mock(JsonNode.class);
        when(jsonObject.has("after")).thenReturn(true);
        when(jsonObject.get("after")).thenReturn(mock(JsonNode.class));

        Checkout checkout = new Checkout();
        checkout.setId("12345");
        when(checkoutService.findCheckoutById(anyString())).thenReturn(checkout);

        BadRequestException badRequestException = new BadRequestException("test exception");
        when(paymentService.createPaymentFromEvent(any())).thenThrow(badRequestException);

        BadRequestException badRequest
            = Assertions.assertThrows(BadRequestException.class, () -> orderStatusConsumer.listen(consumerRecord));
        assertThat(badRequest.getMessage()).isEqualTo("Failed to process checkout event for ID 12345");

        ArgumentCaptor<Checkout> argumentCaptor = ArgumentCaptor.forClass(Checkout.class);
        verify(checkoutService, atLeastOnce()).findCheckoutById(any());
        verify(checkoutService, atLeastOnce()).updateCheckout(argumentCaptor.capture());
        verify(paymentService, atLeastOnce()).createPaymentFromEvent(any());

        Checkout actual = argumentCaptor.getValue();
        assertThat(actual.getProgress()).isEqualTo(CheckoutProgress.PAYMENT_CREATED_FAILED);
        assertThat(actual.getLastError())
            .isEqualTo("{\"errorCode\":\"PAYMENT_CREATED_FAILED\",\"message\":\"test exception\"}");

    }
}
