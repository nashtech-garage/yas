package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private final TestFallbackHandler handler = new TestFallbackHandler();

    @Test
    void handleBodilessFallback_ThrowsOriginalThrowable() {
        RuntimeException exception = new RuntimeException("fallback-error");

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> handler.callBodiless(exception));

        assertSame(exception, thrown);
    }

    @Test
    void handleTypedFallback_ThrowsOriginalThrowable() {
        IllegalStateException exception = new IllegalStateException("typed-fallback-error");

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> handler.callTyped(exception));

        assertSame(exception, thrown);
    }

    private static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        void callBodiless(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }

        String callTyped(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }
}
