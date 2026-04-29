package com.yas.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private final AbstractCircuitBreakFallbackHandler handler = new AbstractCircuitBreakFallbackHandler() {};

    @Test
    void handleBodilessFallback_ShouldThrowException() {
        Throwable throwable = new RuntimeException("Test Exception");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> handler.handleBodilessFallback(throwable));
        assertEquals("Test Exception", exception.getMessage());
    }

    @Test
    void handleTypedFallback_ShouldThrowException() {
        Throwable throwable = new RuntimeException("Test Exception");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> handler.handleTypedFallback(throwable));
        assertEquals("Test Exception", exception.getMessage());
    }
}
