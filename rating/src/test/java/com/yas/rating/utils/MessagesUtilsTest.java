package com.yas.rating.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessagesUtilsTest {

    @Test
    void testGetMessage_whenValidErrorCode_shouldReturnMessage() {
        // Given
        String errorCode = "RATING_NOT_FOUND";

        // When
        String message = MessagesUtils.getMessage(errorCode);

        // Then
        assertThat(message).isNotNull();
        assertThat(message).isNotEmpty();
    }

    @Test
    void testGetMessage_whenInvalidErrorCode_shouldReturnErrorCodeItself() {
        // Given
        String errorCode = "NON_EXISTENT_ERROR_CODE";

        // When
        String message = MessagesUtils.getMessage(errorCode);

        // Then
        assertThat(message).isEqualTo(errorCode);
    }

    @Test
    void testGetMessage_withParameters_shouldFormatMessage() {
        // Given
        String errorCode = "RATING_NOT_FOUND";
        Object param1 = "123";

        // When
        String message = MessagesUtils.getMessage(errorCode, param1);

        // Then
        assertThat(message).isNotNull();
        assertThat(message).isNotEmpty();
    }

    @Test
    void testGetMessage_withMultipleParameters_shouldFormatMessage() {
        // Given
        String errorCode = "RATING_NOT_FOUND";
        Object param1 = "product1";
        Object param2 = "user1";

        // When
        String message = MessagesUtils.getMessage(errorCode, param1, param2);

        // Then
        assertThat(message).isNotNull();
        assertThat(message).isNotEmpty();
    }
}
