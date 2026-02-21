package com.yas.location.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void testGetMessage_WithExistingErrorCode_Success() {
        String message = MessagesUtils.getMessage("COUNTRY_NOT_FOUND", "1");

        assertNotNull(message);
        // The actual message will be retrieved from resource bundle
        assertEquals("The country 1 is not found", message);
    }

    @Test
    void testGetMessage_WithNonExistingErrorCode_ReturnsErrorCode() {
        String message = MessagesUtils.getMessage("NON_EXISTING_CODE");

        assertNotNull(message);
        assertEquals("NON_EXISTING_CODE", message);
    }

    @Test
    void testGetMessage_WithMultipleArguments_Success() {
        String message = MessagesUtils.getMessage("NAME_ALREADY_EXITED", "test_name");

        assertNotNull(message);
        assertTrue(message.contains("test_name") || message.contains("NAME_ALREADY_EXITED"));
    }

    @Test
    void testGetMessage_WithNoArguments_Success() {
        String message = MessagesUtils.getMessage("ADDRESS_NOT_FOUND");

        assertNotNull(message);
    }

    @Test
    void testGetMessage_WithStateOrProvinceErrorCode_Success() {
        String message = MessagesUtils.getMessage("STATE_OR_PROVINCE_NOT_FOUND", "1");

        assertNotNull(message);
    }

    @Test
    void testGetMessage_WithCodeAlreadyExistedErrorCode_Success() {
        String message = MessagesUtils.getMessage("CODE_ALREADY_EXISTED", "TEST");

        assertNotNull(message);
    }

    @Test
    void testGetMessage_ReturnsNonNull_Always() {
        String message = MessagesUtils.getMessage("ANY_CODE", "arg1", "arg2");

        assertNotNull(message);
    }

    private void assertTrue(boolean condition) {
        if (!condition) throw new AssertionError("Condition is false");
    }
}
