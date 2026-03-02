package com.yas.payment.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessagesUtilsTest {

    @Test
    void testGetMessage_whenValidMessageCode_thenReturnMessage() {
        // Given
        String errorCode = "ERROR_CODE_000";
        
        // When
        String result = MessagesUtils.getMessage(errorCode);
        
        // Then
        assertNotNull(result);
    }

    @Test
    void testGetMessage_whenInvalidMessageCode_thenReturnSameCode() {
        // Given
        String errorCode = "NON_EXISTENT_CODE_12345";
        
        // When
        String result = MessagesUtils.getMessage(errorCode);
        
        // Then
        assertEquals(errorCode, result);
    }

    @Test
    void testGetMessage_withParameters_thenFormatMessage() {
        // Given
        String errorCode = "NON_EXISTENT_CODE";
        Object[] params = {"param1", "param2"};
        
        // When
        String result = MessagesUtils.getMessage(errorCode, params);
        
        // Then
        assertNotNull(result);
    }

    @Test
    void testGetMessage_withNullParameters_thenHandleGracefully() {
        // Given
        String errorCode = "ERROR_CODE";
        
        // When
        String result = MessagesUtils.getMessage(errorCode, (Object[]) null);
        
        // Then
        assertNotNull(result);
    }

    @Test
    void testGetMessage_withEmptyParameters_thenReturnMessage() {
        // Given
        String errorCode = "ERROR_CODE";
        
        // When
        String result = MessagesUtils.getMessage(errorCode);
        
        // Then
        assertNotNull(result);
    }
}
