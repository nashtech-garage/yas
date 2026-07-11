package com.yas.rating.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_whenCodeExistsAndHasPlaceholder_returnFormattedMessage() {
        String message = MessagesUtils.getMessage("CUSTOMER_NOT_FOUND", "123");

        assertThat(message).isEqualTo("CUSTOMER 123 is not found");
    }

    @Test
    void getMessage_whenCodeDoesNotExist_returnErrorCode() {
        String message = MessagesUtils.getMessage("UNKNOWN_CODE");

        assertThat(message).isEqualTo("UNKNOWN_CODE");
    }
}
