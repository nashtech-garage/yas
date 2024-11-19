package com.yas.order.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yas.commonlibrary.exception.BadRequestException;
import java.util.Optional;
import org.slf4j.Logger;

public class JsonUtils {

    private JsonUtils() {
        throw new UnsupportedOperationException();
    }

    public static String convertObjectToString(ObjectMapper objectMapper, Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BadRequestException(Constants.ErrorCode.CANNOT_CONVERT_TO_STRING, value);
        }
    }

    public static ObjectNode getAttributesNode(ObjectMapper objectMapper, String attributes) {
        try {
            if (attributes == null || attributes.isBlank()) {
                return objectMapper.createObjectNode();
            } else {
                return (ObjectNode) objectMapper.readTree(attributes);
            }
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid Json: {}", attributes);
        }
    }

    public static ObjectNode createJsonErrorObject(ObjectMapper objectMapper, String errorCode, String message) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("errorCode", errorCode);
        objectNode.put("message", message);
        return objectNode;
    }

    public static String getJsonValueOrThrow(
        JsonNode jsonObject,
        String columnName,
        String errorCode,
        Object... errorParams
    ) {
        return Optional.ofNullable(jsonObject.get(columnName))
            .map(JsonNode::asText)
            .orElseThrow(() -> new BadRequestException(errorCode, errorParams));
    }

    public static String getJsonValueOrNull(
        JsonNode jsonObject,
        String columnName
    ) {
        JsonNode jsonElement = jsonObject.get(columnName);
        if (jsonElement != null && !jsonElement.isNull()) {
            return jsonElement.asText();
        }
        return null;
    }


    public static JsonNode getJsonNodeByValue(
        ObjectMapper objectMapper,
        String jsonValue,
        Logger logger
    ) {
        try {
            return objectMapper.readTree(jsonValue);
        } catch (JsonProcessingException e) {
            logger.error("Invalid JSON. Message: {}", jsonValue, e);
            throw new BadRequestException("Failed to parse message as JSON. Message: {}", jsonValue);
        }
    }
}