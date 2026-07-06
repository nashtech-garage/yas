package com.yas.tax.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.tax.constants.MessageCode;
import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessageShouldResolveKnownCode() {
        String message = MessagesUtils.getMessage(MessageCode.TAX_CLASS_NOT_FOUND, 10L);

        assertThat(message).isEqualTo(MessageCode.TAX_CLASS_NOT_FOUND);
    }

    @Test
    void getMessageShouldFallbackToCodeWhenMissing() {
        String message = MessagesUtils.getMessage("missing.tax.message");

        assertThat(message).isEqualTo("missing.tax.message");
    }
}
