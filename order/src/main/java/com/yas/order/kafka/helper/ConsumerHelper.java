package com.yas.order.kafka.helper;

import static com.yas.order.utils.JsonUtils.getJsonNodeByValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;

public class ConsumerHelper {

    private ConsumerHelper() {
        throw new UnsupportedOperationException();
    }

    public static void processForEventUpdate(ConsumerRecord<?, ?> consumerRecord,
                                             Consumer<JsonNode> eventHandler,
                                             ObjectMapper objectMapper,
                                             Logger logger
                                             ) {

        JsonNode valueObject = getValueFromConsumerRecord(consumerRecord, objectMapper, logger);
        Optional.ofNullable(valueObject)
            .filter(value -> value.has("op") && "u".equals(value.get("op").asText()))
            .filter(value -> value.has("before") && value.has("after"))
            .ifPresentOrElse(
                eventHandler,
                () -> logger.warn("Message does not match expected update structure: {}", valueObject)
            );
    }

    public static JsonNode getValueFromConsumerRecord(
        ConsumerRecord<?, ?> consumerRecord,
        ObjectMapper objectMapper,
        Logger logger
    ) {

        if (Objects.isNull(consumerRecord)) {
            logger.info("ConsumerRecord is null");
            return null;
        }

        String jsonValue = (String) consumerRecord.value();
        return getJsonNodeByValue(objectMapper, jsonValue, logger);
    }
}
