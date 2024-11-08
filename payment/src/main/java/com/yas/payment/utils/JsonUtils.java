package com.yas.payment.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yas.commonlibrary.exception.BadRequestException;

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
}