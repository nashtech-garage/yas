package com.yas.rating.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private final AbstractCircuitBreakFallbackHandler handler = new AbstractCircuitBreakFallbackHandler() {};

    @Test
    void handleFallback_WhenCalled_ShouldLogAndThrow() {
        Throwable t = new RuntimeException("Test error");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> handler.handleFallback(t));
        assertEquals("Test error", exception.getMessage());
    }

    @Test
    void handleBodilessFallback_WhenCalled_ShouldLogAndThrow() {
        Throwable t = new RuntimeException("Test error");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> handler.handleBodilessFallback(t));
        assertEquals("Test error", exception.getMessage());
    }
}
