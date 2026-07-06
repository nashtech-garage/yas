package com.yas.webhook.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;

class HmacUtilsTest {

    @Test
    void hashShouldReturnDeterministicHmacBytes() throws Exception {
        String first = HmacUtils.hash("payload", "secret");
        String second = HmacUtils.hash("payload", "secret");

        assertThat(first).isEqualTo(second);
        assertThat(first.getBytes(StandardCharsets.UTF_8)).isNotEmpty();
        assertThat(Base64.getEncoder().encodeToString(first.getBytes(StandardCharsets.UTF_8))).isNotBlank();
    }
}
