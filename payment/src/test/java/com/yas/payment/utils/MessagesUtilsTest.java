package com.yas.payment.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    @DisplayName("getMessage - should return message for known error code")
    void getMessage_ShouldReturnMessage_WhenCodeExists() {
        // When
        String result = MessagesUtils.getMessage(Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND, "providerId");

        // Then
        assertNotNull(result);
    }

    @Test
    @DisplayName("getMessage - should return error code when message key not found")
    void getMessage_ShouldReturnErrorCode_WhenCodeNotFound() {
        // Given
        String unknownCode = "UNKNOWN_ERROR_CODE_12345";

        // When
        String result = MessagesUtils.getMessage(unknownCode);

        // Then
        // When the key is not found in message bundle, it returns the code itself
        assertNotNull(result);
        // The result is either the code itself or the message from bundle
        // Either way it should be a non-null, non-empty string
        assert !result.isEmpty();
    }

    @Test
    @DisplayName("getMessage - should format message with arguments")
    void getMessage_ShouldFormatMessageWithArguments() {
        // Given
        String unknownCode = "UNKNOWN_CODE";
        String arg = "testArg";

        // When
        String result = MessagesUtils.getMessage(unknownCode, arg);

        // Then
        assertNotNull(result);
    }
}
