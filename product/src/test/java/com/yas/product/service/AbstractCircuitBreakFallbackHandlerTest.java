package com.yas.product.service;

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
        RuntimeException exception = new RuntimeException("product-fallback-error");

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> handler.executeBodiless(exception));

        assertEquals("product-fallback-error", thrown.getMessage());
    }

    @Test
    void handleTypedFallback_whenException_thenRethrow() {
        TestFallbackHandler handler = new TestFallbackHandler();
        IllegalArgumentException exception = new IllegalArgumentException("typed-product-fallback-error");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> handler.executeTyped(exception));

        assertEquals("typed-product-fallback-error", thrown.getMessage());
    }
}
