package com.yas.order.kafka.consumer;

import static com.yas.order.kafka.helper.ConsumerHelper.processForEventUpdate;
import static com.yas.order.utils.JsonUtils.getJsonValueOrNull;
import static com.yas.order.utils.JsonUtils.getJsonValueOrThrow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.order.model.Checkout;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.service.CheckoutService;
import com.yas.order.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

/**
 * After Status Payment is changed to COMPLETED/FAILURE.
 * Update Status Checkout to PAYMENT_CONFIRMED
 */
@Service
@RequiredArgsConstructor
public class PaymentStatusUpdateConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentStatusUpdateConsumer.class);
    private final CheckoutService checkoutService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = "${cdc.event.payment.topic-name}",
        groupId = "${cdc.event.payment.status.update.group-id}"
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

        JsonNode beforeObject = valueObject.get("before");
        JsonNode afterObject = valueObject.get("after");

        String id = getJsonValueOrThrow(afterObject, Constants.Column.ID_COLUMN,
            Constants.ErrorCode.ID_NOT_EXISTED);

        String beforePaymentStatus = getJsonValueOrNull(beforeObject,
            Constants.Column.PAYMENT_STATUS_FIELD);
        String afterPaymentStatus = getJsonValueOrNull(afterObject,
            Constants.Column.PAYMENT_STATUS_FIELD);

        if ((PaymentStatus.COMPLETED.name().equals(afterPaymentStatus)
            || PaymentStatus.FAILURE.name().equals(afterPaymentStatus))
            && !afterPaymentStatus.equals(beforePaymentStatus)) {

            LOGGER.info("Update Checkout Payment Status to PAYMENT_CONFIRMED with Payment {}", id);

            String checkoutId = getJsonValueOrThrow(afterObject, Constants.Column.CHECKOUT_ID_COLUMN,
                Constants.ErrorCode.CHECKOUT_ID_NOT_EXISTED);
            updateCheckoutPaymentStatus(checkoutId);
        } else {
            LOGGER.warn("It is not an event to confirm payment with Payment ID {}", id);
        }
    }

    private void updateCheckoutPaymentStatus(String checkoutId) {

        Checkout checkout = checkoutService.findCheckoutById(checkoutId);
        checkout.setCheckoutState(CheckoutState.PAYMENT_CONFIRMED);
        checkoutService.updateCheckout(checkout);
    }

}