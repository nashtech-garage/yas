package com.yas.order.kafka.consumer;

import static com.yas.order.kafka.helper.ConsumerHelper.processForEventUpdate;
import static com.yas.order.utils.JsonUtils.convertObjectToString;
import static com.yas.order.utils.JsonUtils.createJsonErrorObject;
import static com.yas.order.utils.JsonUtils.getAttributesNode;
import static com.yas.order.utils.JsonUtils.getJsonValueOrThrow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.order.model.Checkout;
import com.yas.order.model.enumeration.CheckoutProgress;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.service.CheckoutService;
import com.yas.order.service.PaymentService;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.payment.CheckoutPaymentVm;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

/**
 * After fulfillment, process payment and create order in PayPal.
 */
@Service
@RequiredArgsConstructor
public class CheckoutFulfillmentConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckoutFulfillmentConsumer.class);
    private final PaymentService paymentService;
    private final CheckoutService checkoutService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = "${cdc.event.checkout.status.topic-name}",
        groupId = "${cdc.event.checkout.fulfillment.group-id}"
    )
    @RetryableTopic(
        attempts = "1"
    )
    public void listen(ConsumerRecord<?, ?> consumerRecord) {
        processForEventUpdate(
            consumerRecord,
            this::handleAfterJson,
            objectMapper,
            LOGGER
        );
    }

    private void handleAfterJson(JsonNode valueObject) {
        JsonNode afterObject = valueObject.get("after");
        String id = getJsonValueOrThrow(afterObject, Constants.Column.ID_COLUMN,
            Constants.ErrorCode.ID_NOT_EXISTED);
        String status = getJsonValueOrThrow(afterObject, Constants.Column.STATUS_COLUMN,
            Constants.ErrorCode.STATUS_NOT_EXISTED, id);
        String progress = getJsonValueOrThrow(afterObject, Constants.Column.CHECKOUT_PROGRESS_COLUMN,
            Constants.ErrorCode.PROGRESS_NOT_EXISTED, id);

        if (!isPaymentProcessing(status, progress)) {
            LOGGER.info("Checkout record with ID {} lacks the status 'PAYMENT_PROCESSING' and progress 'STOCK_LOCKED'",
                id);
            return;
        }

        LOGGER.info("Checkout record with ID {} has the status 'PAYMENT_PROCESSING' and the process 'STOCK_LOCKED'",
            id);

        Checkout checkout = checkoutService.findCheckoutById(id);

        processPaymentAndUpdateCheckout(checkout);
    }

    private boolean isPaymentProcessing(String status, String process) {
        return CheckoutState.PAYMENT_PROCESSING.name().equalsIgnoreCase(status)
            && CheckoutProgress.STOCK_LOCKED.name().equalsIgnoreCase(process);
    }

    private void processPaymentAndUpdateCheckout(Checkout checkout) {

        try {
            Long paymentId = processPayment(checkout);
            checkout.setProgress(CheckoutProgress.PAYMENT_CREATED);
            checkout.setLastError(null);

            ObjectNode updatedAttributes = updateAttributesWithPayment(checkout.getAttributes(), paymentId);
            checkout.setAttributes(convertObjectToString(objectMapper, updatedAttributes));

        } catch (Exception e) {

            checkout.setProgress(CheckoutProgress.PAYMENT_CREATED_FAILED);

            ObjectNode error = createJsonErrorObject(objectMapper, CheckoutProgress.PAYMENT_CREATED_FAILED.name(),
                e.getMessage());
            checkout.setLastError(convertObjectToString(objectMapper, error));

            LOGGER.error(e.getMessage());
            throw new BadRequestException(Constants.ErrorCode.PROCESS_CHECKOUT_FAILED, checkout.getId());

        } finally {
            checkoutService.updateCheckout(checkout);
        }
    }

    private Long processPayment(Checkout checkout) {

        CheckoutPaymentVm requestDto = new CheckoutPaymentVm(
            checkout.getId(),
            checkout.getPaymentMethodId(),
            checkout.getTotalAmount()
        );

        Long paymentId = paymentService.createPaymentFromEvent(requestDto);
        LOGGER.info("Payment created successfully with ID: {}", paymentId);

        return paymentId;
    }

    private ObjectNode updateAttributesWithPayment(String attributes, Long paymentId) {

        ObjectNode attributesNode = getAttributesNode(objectMapper, attributes);
        attributesNode.put(Constants.Column.CHECKOUT_ATTRIBUTES_PAYMENT_ID_FIELD, paymentId);

        return attributesNode;
    }

}
