package com.yas.webhook.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class HmacUtilsTest {

    @Test
    void hash_ValidDataAndKey_ShouldReturnHmacString() throws Exception {
        String data = "testData";
        String key = "testKey";
        String result = HmacUtils.hash(data, key);
        assertNotNull(result);
    }
}
