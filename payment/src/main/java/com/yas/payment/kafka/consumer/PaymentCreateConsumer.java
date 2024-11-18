package com.yas.payment.kafka.consumer;

import static com.yas.payment.utils.JsonUtils.convertObjectToString;
import static com.yas.payment.utils.JsonUtils.getAttributesNode;
import static com.yas.payment.utils.JsonUtils.getJsonNodeByValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.service.PaymentService;
import com.yas.payment.utils.Constants;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
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
public class PaymentCreateConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentCreateConsumer.class);
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = "${cdc.event.payment.topic-name}",
        groupId = "${cdc.event.payment.create.group-id}"
    )
    @RetryableTopic
    public void listen(ConsumerRecord<?, ?> consumerRecord) {

        if (Objects.isNull(consumerRecord)) {
            LOGGER.info("Consumer Record is null");
            return;
        }
        String jsonValue = (String) consumerRecord.value();
        JsonNode valueObject = getJsonNodeByValue(objectMapper, jsonValue, LOGGER);
        processCheckoutEvent(valueObject);
    }

    private void processCheckoutEvent(JsonNode valueObject) {
        Optional.ofNullable(valueObject)
            .filter(
                value -> value.has("op") && "c".equals(value.get("op").asText())
            )
            .filter(value -> value.has("after"))
            .map(value -> value.get("after"))
            .ifPresent(this::handleAfterJsonForCreatingOrder);
    }

    private void handleAfterJsonForCreatingOrder(JsonNode after) {

        Long id = Optional.ofNullable(after.get(Constants.Column.ID_COLUMN))
            .filter(jsonElement -> !jsonElement.isNull())
            .map(JsonNode::asLong)
            .orElseThrow(() -> new BadRequestException(Constants.ErrorCode.ID_NOT_EXISTED));

        LOGGER.info("Handle after json for creating order Payment ID {}", id);

        Payment payment = paymentService.findPaymentById(id);

        if (PaymentMethod.COD.equals(payment.getPaymentMethod())) {
            LOGGER.warn(Constants.Message.PAYMENT_METHOD_COD, payment.getId());
            return;
        }

        if (PaymentMethod.PAYPAL.equals(payment.getPaymentMethod())) {
            LOGGER.info(Constants.Message.PAYMENT_METHOD_PAYPAL, payment.getId());
            createOrderOnPaypal(payment);
        } else {
            LOGGER.warn("Currently only support payment method is Paypal");
        }
    }

    private void createOrderOnPaypal(Payment payment) {

        InitPaymentRequestVm initPaymentRequestVm = InitPaymentRequestVm.builder()
            .paymentMethod(payment.getPaymentMethod().name())
            .totalPrice(payment.getAmount())
            .checkoutId(payment.getCheckoutId()).build();
        InitPaymentResponseVm initPaymentResponseVm = paymentService.initPayment(initPaymentRequestVm);

        if ("success".equals(initPaymentResponseVm.status())) {

            payment.setPaymentStatus(PaymentStatus.PROCESSING);
            payment.setPaymentProviderCheckoutId(initPaymentResponseVm.paymentId());

            ObjectNode attributesNode = getAttributesNode(objectMapper, payment.getAttributes());
            attributesNode.put(Constants.Column.REDIRECT_URL_ID_COLUMN, initPaymentResponseVm.redirectUrl());
            payment.setAttributes(convertObjectToString(objectMapper, attributesNode));

            paymentService.updatePayment(payment);
        } else {
            LOGGER.warn(Constants.ErrorCode.ORDER_CREATION_FAILED, payment.getId());
        }
    }

}