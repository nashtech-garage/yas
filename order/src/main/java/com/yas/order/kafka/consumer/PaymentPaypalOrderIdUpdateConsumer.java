package com.yas.order.kafka.consumer;

import static com.yas.order.kafka.helper.ConsumerHelper.processForEventUpdate;
import static com.yas.order.utils.JsonUtils.convertObjectToString;
import static com.yas.order.utils.JsonUtils.getAttributesNode;
import static com.yas.order.utils.JsonUtils.getJsonValueOrNull;
import static com.yas.order.utils.JsonUtils.getJsonValueOrThrow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yas.order.model.Checkout;
import com.yas.order.model.enumeration.CheckoutProgress;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.service.CheckoutService;
import com.yas.order.utils.Constants;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

/**
 * After the PayPal Order id is updated in payment_provider_checkout_id column.
 * Update Checkout state is PAYMENT_PROCESSING, progress is PAYMENT_CREATED
 */
@Service
@RequiredArgsConstructor
public class PaymentPaypalOrderIdUpdateConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentPaypalOrderIdUpdateConsumer.class);
    private final CheckoutService checkoutService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = "${cdc.event.payment.topic-name}",
        groupId = "${cdc.event.payment.order.id.update.group-id}"
    )
    @RetryableTopic
    public void listen(ConsumerRecord<?, ?> consumerRecord) {
        processForEventUpdate(
            consumerRecord,
            this::handleJsonForUpdateCheckout,
            objectMapper,
            LOGGER
        );
    }

    private void handleJsonForUpdateCheckout(JsonNode valueObject) {

        JsonNode before = valueObject.get("before");
        JsonNode after = valueObject.get("after");

        String id = getJsonValueOrThrow(after, Constants.Column.ID_COLUMN,
            Constants.ErrorCode.ID_NOT_EXISTED);

        String beforePaypalOrderId = getJsonValueOrNull(before,
            Constants.Column.PAYMENT_ATTRIBUTES_PAYMENT_PROVIDER_CHECKOUT_ID_FIELD);
        String afterPaypalOrderId = getJsonValueOrNull(after,
            Constants.Column.PAYMENT_ATTRIBUTES_PAYMENT_PROVIDER_CHECKOUT_ID_FIELD);

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

        ObjectNode attributesNode = getAttributesNode(objectMapper, checkout.getAttributes());
        attributesNode.put(Constants.Column.PAYMENT_ATTRIBUTES_PAYMENT_PROVIDER_CHECKOUT_ID_FIELD,
            paymentProviderCheckoutId);
        checkout.setAttributes(convertObjectToString(objectMapper, attributesNode));

        checkoutService.updateCheckout(checkout);
    }

}