package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        void executeBodiless(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }

        <T> T executeTyped(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }

    @Test
    void handleBodilessFallback_whenException_thenRethrow() {
        TestFallbackHandler handler = new TestFallbackHandler();
        RuntimeException exception = new RuntimeException("order-fallback-error");

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> handler.executeBodiless(exception));

        assertEquals("order-fallback-error", thrown.getMessage());
    }

    @Test
    void handleTypedFallback_whenException_thenRethrow() {
        TestFallbackHandler handler = new TestFallbackHandler();
        IllegalStateException exception = new IllegalStateException("typed-order-fallback-error");

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handler.executeTyped(exception));

        assertEquals("typed-order-fallback-error", thrown.getMessage());
    }
}
