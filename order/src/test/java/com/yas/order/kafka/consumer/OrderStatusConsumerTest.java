package com.yas.order.kafka.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.model.Checkout;
import com.yas.order.model.enumeration.CheckoutProgress;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.service.PaymentService;
import java.util.Optional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class OrderStatusConsumerTest {

    private PaymentService paymentService;

    private CheckoutRepository checkoutRepository;

    private OrderStatusConsumer orderStatusConsumer;

    private final String jsonRecord = "{\"after\": {"
        + " \"status\": \"PAYMENT_PROCESSING\","
        + " \"progress\": \"STOCK_LOCKED\","
        + " \"id\": 12345,"
        + " \"payment_method_id\":  \"PAYPAL\","
        + " \"total_amount\": 123}"
        + "}";

    @BeforeEach
    void setUp() {
        paymentService = Mockito.mock(PaymentService.class);
        checkoutRepository = Mockito.mock(CheckoutRepository.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Gson gson = new Gson();
        orderStatusConsumer = new OrderStatusConsumer(paymentService, checkoutRepository, objectMapper, gson);

    }

    @Test
    void testListen_whenConsumerRecordIsNull_shouldNotThing() {

        orderStatusConsumer.listen(null);

        verify(checkoutRepository, never()).findById(any());
        verify(paymentService, never()).createPaymentFromEvent(any());
        verify(checkoutRepository, never()).save(any());
    }

    @Test
    void testListen_whenHaveNoAfter_shouldNotThing() {

        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);

        when(consumerRecord.value()).thenReturn("{}");

        orderStatusConsumer.listen(consumerRecord);

        verify(checkoutRepository, never()).save(any());
        verify(checkoutRepository, never()).findById(any());
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

        verify(checkoutRepository, never()).findById(any());
        verify(checkoutRepository, never()).save(any());
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

        verify(checkoutRepository, never()).findById(any());
        verify(checkoutRepository, never()).save(any());
        verify(paymentService, never()).createPaymentFromEvent(any());
    }

    @Test
    void testListen_whenCheckoutIsNotFound_shouldNotSave() {

        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);

        when(consumerRecord.value()).thenReturn(jsonRecord);
        JsonObject jsonObject = mock(JsonObject.class);
        when(jsonObject.has("after")).thenReturn(true);
        when(jsonObject.getAsJsonObject("after")).thenReturn(mock(JsonObject.class));

        when(checkoutRepository.findById(anyString())).thenReturn(Optional.empty());

        when(paymentService.createPaymentFromEvent(any())).thenReturn(1L);

        NotFoundException notFoundException
            = Assertions.assertThrows(NotFoundException.class, () -> orderStatusConsumer.listen(consumerRecord));

        assertThat(notFoundException.getMessage()).isEqualTo("Checkout 12345 is not found");
        verify(checkoutRepository, atLeastOnce()).findById(any());
        verify(checkoutRepository, never()).save(any());
        verify(paymentService, never()).createPaymentFromEvent(any());
    }

    @Test
    void testListen_whenCreatePaymentSuccess_shouldProcessCheckoutEvent() {

        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);

        when(consumerRecord.value()).thenReturn(jsonRecord);
        JsonObject jsonObject = mock(JsonObject.class);
        when(jsonObject.has("after")).thenReturn(true);
        when(jsonObject.getAsJsonObject("after")).thenReturn(mock(JsonObject.class));

        ArgumentCaptor<Checkout> argumentCaptor = ArgumentCaptor.forClass(Checkout.class);

        when(checkoutRepository.findById(anyString())).thenReturn(Optional.of(new Checkout()));

        when(paymentService.createPaymentFromEvent(any())).thenReturn(2L);

        orderStatusConsumer.listen(consumerRecord);

        verify(checkoutRepository, atLeastOnce()).findById(any());
        verify(paymentService, atLeastOnce()).createPaymentFromEvent(any());
        verify(checkoutRepository, atLeastOnce()).save(argumentCaptor.capture());

        Checkout actual = argumentCaptor.getValue();
        assertThat(actual.getProgress()).isEqualTo(CheckoutProgress.PAYMENT_CREATED);
        assertThat(actual.getAttributes()).isEqualTo("{\"payment_id\":2}");
    }

    @Test
    void testListen_whenCreatePaymentFailure_shouldThrowException() {

        ConsumerRecord<?, String> consumerRecord = mock(ConsumerRecord.class);

        when(consumerRecord.value()).thenReturn(jsonRecord);
        JsonObject jsonObject = mock(JsonObject.class);
        when(jsonObject.has("after")).thenReturn(true);
        when(jsonObject.getAsJsonObject("after")).thenReturn(mock(JsonObject.class));

        Checkout checkout = new Checkout();
        checkout.setId("12345");
        when(checkoutRepository.findById(anyString())).thenReturn(Optional.of(checkout));

        BadRequestException badRequestException = new BadRequestException("test exception");
        when(paymentService.createPaymentFromEvent(any())).thenThrow(badRequestException);

        BadRequestException badRequest
            = Assertions.assertThrows(BadRequestException.class, () -> orderStatusConsumer.listen(consumerRecord));
        assertThat(badRequest.getMessage()).isEqualTo("Failed to process checkout event for ID 12345");

        ArgumentCaptor<Checkout> argumentCaptor = ArgumentCaptor.forClass(Checkout.class);
        verify(checkoutRepository, atLeastOnce()).findById(any());
        verify(checkoutRepository, atLeastOnce()).save(argumentCaptor.capture());
        verify(paymentService, atLeastOnce()).createPaymentFromEvent(any());

        Checkout actual = argumentCaptor.getValue();
        assertThat(actual.getProgress()).isEqualTo(CheckoutProgress.PAYMENT_CREATED_FAILED);
        assertThat(actual.getLastError())
            .isEqualTo("{\"errorCode\":\"PAYMENT_CREATED_FAILED\",\"message\":\"test exception\"}");

    }
}
