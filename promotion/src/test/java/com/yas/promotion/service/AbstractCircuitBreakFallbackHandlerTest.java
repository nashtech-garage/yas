package com.yas.promotion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private final AbstractCircuitBreakFallbackHandler handler = new AbstractCircuitBreakFallbackHandler() {};

    @Test
    void handleFallback_WhenCalled_ShouldLogAndThrow() {
        Throwable t = new RuntimeException("Test error");
        List<Long> ids = List.of(1L);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> handler.handleFallback(ids, t));
        assertEquals("Test error", exception.getMessage());
    }

    @Test
    void handleBodilessFallback_WhenCalled_ShouldLogAndThrow() {
        Throwable t = new RuntimeException("Test error");
        List<Long> ids = List.of(1L);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> handler.handleBodilessFallback(ids, t));
        assertEquals("Test error", exception.getMessage());
    }
}
