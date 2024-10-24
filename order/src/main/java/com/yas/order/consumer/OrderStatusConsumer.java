package com.yas.order.consumer;

import static com.yas.order.utils.JsonUtils.convertObjectToString;
import static com.yas.order.utils.JsonUtils.createJsonErrorObject;
import static com.yas.order.utils.JsonUtils.getAttributesNode;
import static com.yas.order.utils.JsonUtils.getJsonValueOrThrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.model.Checkout;
import com.yas.order.model.enumeration.CheckoutProgress;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.service.PaymentService;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.payment.CheckoutPaymentVm;
import java.io.IOException;
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
public class OrderStatusConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderStatusConsumer.class);
    private final PaymentService paymentService;
    private final CheckoutRepository checkoutRepository;
    private final ObjectMapper objectMapper;
    private final Gson gson;

    @KafkaListener(
        topics = "${cdc.event.checkout.status.topic-name}",
        groupId = "${cdc.event.checkout.status.group-id}"
    )
    @RetryableTopic(
        attempts = "1"
    )
    public void listen(ConsumerRecord<?, ?> consumerRecord) {

        if (Objects.isNull(consumerRecord)) {
            LOGGER.info("ConsumerRecord is null");
            return;
        }
        JsonObject valueObject = gson.fromJson((String) consumerRecord.value(), JsonObject.class);
        processCheckoutEvent(valueObject);

    }

    private void processCheckoutEvent(JsonObject valueObject) {
        Optional.ofNullable(valueObject)
            .filter(value -> value.has("after"))
            .map(value -> value.getAsJsonObject("after"))
            .ifPresent(this::handleAfterJson);
    }

    private void handleAfterJson(JsonObject after) {

        String id = getJsonValueOrThrow(after, Constants.Column.ID_COLUMN,
            Constants.ErrorCode.ID_NOT_EXISTED);
        String status = getJsonValueOrThrow(after, Constants.Column.STATUS_COLUMN,
            Constants.ErrorCode.STATUS_NOT_EXISTED, id);
        String progress = getJsonValueOrThrow(after, Constants.Column.CHECKOUT_PROGRESS_COLUMN,
            Constants.ErrorCode.PROGRESS_NOT_EXISTED, id);

        if (!isPaymentProcessing(status, progress)) {
            LOGGER.info("Checkout record with ID {} lacks the status 'PAYMENT_PROCESSING' and progress 'STOCK_LOCKED'",
                id);
            return;
        }

        LOGGER.info("Checkout record with ID {} has the status 'PAYMENT_PROCESSING' and the process 'STOCK_LOCKED'",
            id);

        Checkout checkout = checkoutRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.CHECKOUT_NOT_FOUND, id));

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
            checkoutRepository.save(checkout);
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

    private ObjectNode updateAttributesWithPayment(String attributes, Long paymentId) throws IOException {

        ObjectNode attributesNode = getAttributesNode(objectMapper, attributes);
        attributesNode.put(Constants.Column.CHECKOUT_ATTRIBUTES_PAYMENT_ID_FIELD, paymentId);

        return attributesNode;
    }

}
