package com.yas.webhook.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class HmacUtilsTest {

    @Test
    void hash_ShouldReturnHash() throws Exception {
        String data = "data";
        String key = "key";
        String result = HmacUtils.hash(data, key);
        assertNotNull(result);
    }
}
