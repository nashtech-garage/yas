package com.yas.webhook.utils;

import org.junit.jupiter.api.Test;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HmacUtilsTest {

    @Test
    void hash_shouldReturnHash() throws NoSuchAlgorithmException, InvalidKeyException {
        String data = "test-data";
        String key = "test-key";
        String result = HmacUtils.hash(data, key);
        assertNotNull(result);
    }
}
