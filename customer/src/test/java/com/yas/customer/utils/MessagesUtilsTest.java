package com.yas.customer.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_WhenCodeExists_ShouldReturnMessage() {
        String message = MessagesUtils.getMessage("SOME_DUMMY_CODE");
        assertEquals("SOME_DUMMY_CODE", message);
    }
}
