package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private final AbstractCircuitBreakFallbackHandler handler = new AbstractCircuitBreakFallbackHandler() {};

    @Test
    void handleBodilessFallback_shouldThrowOriginalException() {
        Throwable throwable = new RuntimeException("Bodiless test");
        assertThrows(RuntimeException.class, () -> handler.handleBodilessFallback(throwable));
    }

    @Test
    void handleTypedFallback_shouldThrowOriginalException() {
        Throwable throwable = new RuntimeException("Typed test");
        assertThrows(RuntimeException.class, () -> handler.handleTypedFallback(throwable));
    }
}
