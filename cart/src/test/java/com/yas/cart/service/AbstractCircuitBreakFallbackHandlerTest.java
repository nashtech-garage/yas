package com.yas.cart.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    // Concrete subclass để test abstract class
    private static class TestHandler extends AbstractCircuitBreakFallbackHandler {
        public void callBodilessFallback(Throwable t) throws Throwable {
            handleBodilessFallback(t);
        }

        public <T> T callTypedFallback(Throwable t) throws Throwable {
            return handleTypedFallback(t);
        }
    }

    private final TestHandler handler = new TestHandler();

    @Test
    void testHandleBodilessFallback_shouldRethrowException() {
        RuntimeException exception = new RuntimeException("circuit breaker triggered");

        assertThrows(RuntimeException.class, () -> handler.callBodilessFallback(exception));
    }

    @Test
    void testHandleTypedFallback_shouldRethrowException() {
        RuntimeException exception = new RuntimeException("typed fallback triggered");

        assertThrows(RuntimeException.class, () -> handler.callTypedFallback(exception));
    }
}
