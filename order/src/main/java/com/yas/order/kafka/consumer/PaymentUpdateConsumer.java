package com.yas.order.kafka.consumer;

import static com.yas.order.utils.JsonUtils.convertObjectToString;
import static com.yas.order.utils.JsonUtils.getAttributesNode;
import static com.yas.order.utils.JsonUtils.getJsonValueOrNull;
import static com.yas.order.utils.JsonUtils.getJsonValueOrThrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yas.order.model.Checkout;
import com.yas.order.model.enumeration.CheckoutProgress;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.service.CheckoutService;
import com.yas.order.utils.Constants;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentUpdateConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentUpdateConsumer.class);
    private final CheckoutService checkoutService;
    private final ObjectMapper objectMapper;
    private final Gson gson;

    @KafkaListener(
        topics = "${cdc.event.payment.topic-name}",
        groupId = "${cdc.event.payment.update.group-id}"
    )
    @RetryableTopic
    public void listen(ConsumerRecord<?, ?> consumerRecord) {

        if (Objects.isNull(consumerRecord)) {
            LOGGER.info("Consumer Record is null");
            return;
        }
        JsonObject valueObject = gson.fromJson((String) consumerRecord.value(), JsonObject.class);
        processPaymentEvent(valueObject);

    }

    private void processPaymentEvent(JsonObject valueObject) {
        Optional.ofNullable(valueObject)
            .filter(
                value -> value.has("op") && "u".equals(value.get("op").getAsString())
            )
            .filter(value -> value.has("before") && value.has("after"))
            .ifPresent(this::handleJsonForUpdateCheckout);
    }

    private void handleJsonForUpdateCheckout(JsonObject valueObject) {

        JsonObject before = valueObject.getAsJsonObject("before");
        JsonObject after = valueObject.getAsJsonObject("after");

        String id = getJsonValueOrThrow(after, Constants.Column.ID_COLUMN,
            Constants.ErrorCode.ID_NOT_EXISTED);

        String beforePaypalOrderId = getJsonValueOrNull(before,
            Constants.Column.CHECKOUT_ATTRIBUTES_PAYMENT_PROVIDER_CHECKOUT_ID_FIELD);
        String afterPaypalOrderId = getJsonValueOrNull(after,
            Constants.Column.CHECKOUT_ATTRIBUTES_PAYMENT_PROVIDER_CHECKOUT_ID_FIELD);

        if (!Objects.isNull(afterPaypalOrderId) && !afterPaypalOrderId.equals(beforePaypalOrderId)) {

            LOGGER.info("Handle json for update Checkout with Payment {}", id);

            String checkoutId = getJsonValueOrThrow(after, Constants.Column.CHECKOUT_ID_COLUMN,
                Constants.ErrorCode.CHECKOUT_ID_NOT_EXISTED);
            updateCheckOut(checkoutId, afterPaypalOrderId);
        } else {
            LOGGER.warn("It is not an event to update an Order on PayPal with Payment ID {}", id);
        }
    }

    private void updateCheckOut(String checkoutId, String paymentProviderCheckoutId) {

        Checkout checkout = checkoutService.findCheckoutById(checkoutId);
        checkout.setCheckoutState(CheckoutState.PAYMENT_PROCESSING);
        checkout.setProgress(CheckoutProgress.PAYMENT_CREATED);

        ObjectNode updatedAttributes = updateAttributesWithCheckout(checkout.getAttributes(),
            paymentProviderCheckoutId);
        checkout.setAttributes(convertObjectToString(objectMapper, updatedAttributes));

        checkoutService.updateCheckout(checkout);
    }

    private ObjectNode updateAttributesWithCheckout(String attributes, String paymentProviderCheckoutId) {

        ObjectNode attributesNode = getAttributesNode(objectMapper, attributes);
        attributesNode.put(Constants.Column.CHECKOUT_ATTRIBUTES_PAYMENT_PROVIDER_CHECKOUT_ID_FIELD,
            paymentProviderCheckoutId);

        return attributesNode;
    }

}