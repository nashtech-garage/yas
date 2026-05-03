package com.yas.product.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessagesUtilsTest {

    @Test
    void testGetMessage_WithExistingCode() {
        String message = MessagesUtils.getMessage(Constants.ErrorCode.PRODUCT_NOT_FOUND);
        assertNotNull(message);
    }

    @Test
    void testGetMessage_WithMissingCode_ReturnsErrorCode() {
        String fakeCode = "SOME_RANDOM_CODE_THAT_DOES_NOT_EXIST";
        String message = MessagesUtils.getMessage(fakeCode);
        
        assertEquals(fakeCode, message);
    }

    @Test
    void testGetMessage_WithArguments() {
        String errorCode = "Test message with {} format";
        String result = MessagesUtils.getMessage(errorCode, "Spring");
        assertEquals("Test message with Spring format", result);
    }
}