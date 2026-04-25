package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private final TestHandler handler = new TestHandler();

    @Test
    void handleBodilessFallback_shouldRethrowThrowable() {
        Throwable throwable = new RuntimeException("boom");

        assertThrows(RuntimeException.class, () -> handler.callBodiless(throwable));
    }

    @Test
    void handleTypedFallback_shouldRethrowThrowable() {
        Throwable throwable = new RuntimeException("boom");

        assertThrows(RuntimeException.class, () -> handler.callTyped(throwable));
    }

    private static class TestHandler extends AbstractCircuitBreakFallbackHandler {
        void callBodiless(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }

        Object callTyped(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }
}
