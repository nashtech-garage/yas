package com.yas.payment.paypal.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;

class AbstractCircuitBreakFallbackHandlerTest {

    private final AbstractCircuitBreakFallbackHandler handler = new AbstractCircuitBreakFallbackHandler() {};

    @Test
    void handleBodilessFallback_ShouldThrowException() {
        Throwable throwable = new RuntimeException("Fallback error");
        assertThrows(RuntimeException.class, () -> handler.handleBodilessFallback(throwable));
    }

    @Test
    void handleTypedFallback_ShouldThrowException() {
        Throwable throwable = new RuntimeException("Typed fallback error");
        assertThrows(RuntimeException.class, () -> handler.handleTypedFallback(throwable));
    }
}
