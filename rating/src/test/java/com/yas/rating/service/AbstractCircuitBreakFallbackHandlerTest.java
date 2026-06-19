package com.yas.rating.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private AbstractCircuitBreakFallbackHandler handler;

    @BeforeEach
    void setUp() {
        // Concrete anonymous subclass that uses default (base) implementations
        handler = new AbstractCircuitBreakFallbackHandler() {};
    }

    @Test
    void handleBodilessFallback_shouldRethrowException() {
        RuntimeException exception = new RuntimeException("test error");

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> handler.handleBodilessFallback(exception));

        assertEquals("test error", thrown.getMessage());
    }

    @Test
    void handleFallback_shouldRethrowException() {
        RuntimeException exception = new RuntimeException("fallback error");

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> handler.handleFallback(exception));

        assertEquals("fallback error", thrown.getMessage());
    }

    @Test
    void handleBodilessFallback_withCheckedException_shouldRethrow() {
        Exception checkedException = new Exception("checked error");

        Exception thrown = assertThrows(Exception.class,
                () -> handler.handleBodilessFallback(checkedException));

        assertEquals("checked error", thrown.getMessage());
    }

    @Test
    void handleFallback_withCheckedException_shouldRethrow() {
        Exception checkedException = new Exception("checked fallback");

        Exception thrown = assertThrows(Exception.class,
                () -> handler.handleFallback(checkedException));

        assertEquals("checked fallback", thrown.getMessage());
    }
}
