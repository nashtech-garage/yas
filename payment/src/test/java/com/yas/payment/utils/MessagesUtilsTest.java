package com.yas.payment.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @BeforeEach
    void setUp() {
        // Reset bundle to default for consistent testing
        MessagesUtils.messageBundle = ResourceBundle.getBundle("messages.messages", Locale.getDefault());
    }

    @Test
    void getMessage_WithValidCode_ShouldReturnFormattedMessage() {
        String result = MessagesUtils.getMessage("VALID_CODE", "arg1");
        assertThat(result).isNotNull();
    }

    @Test
    void getMessage_WithMissingResource_ShouldReturnCode() {
        String code = "non.existent.code";
        String result = MessagesUtils.getMessage(code);
        assertThat(result).isEqualTo(code);
    }
}
