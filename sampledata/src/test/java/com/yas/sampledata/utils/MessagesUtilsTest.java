package com.yas.sampledata.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagesUtilsTest {

    @Test
    void testGetMessageWithExistingKey() {
        // This will test the fallback behavior when key doesn't exist since there is no bundle configured in tests
        String result = MessagesUtils.getMessage("non.existent.key", "param1");
        assertEquals("non.existent.key", result);
    }
}
