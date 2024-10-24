package com.yas.order.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.yas.commonlibrary.exception.BadRequestException;
import java.io.IOException;
import java.util.Optional;

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

    public static ObjectNode getAttributesNode(ObjectMapper objectMapper, String attributes) throws IOException {
        if (attributes == null || attributes.isBlank()) {
            return objectMapper.createObjectNode();
        } else {
            return (ObjectNode) objectMapper.readTree(attributes);
        }
    }

    public static ObjectNode createJsonErrorObject(ObjectMapper objectMapper, String errorCode, String message) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("errorCode", errorCode);
        objectNode.put("message", message);
        return objectNode;
    }

    public static String getJsonValueOrThrow(
        JsonObject jsonObject,
        String columnName,
        String errorCode,
        Object... errorParams
    ) {
        return Optional.ofNullable(jsonObject.get(columnName))
            .filter(jsonElement -> !jsonElement.isJsonNull())
            .map(JsonElement::getAsString)
            .orElseThrow(() -> new BadRequestException(errorCode, errorParams));
    }
}
