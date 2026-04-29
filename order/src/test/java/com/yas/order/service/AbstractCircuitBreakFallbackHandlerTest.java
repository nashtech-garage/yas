package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    // Concrete subclass for testing the abstract class
    private static class TestHandler extends AbstractCircuitBreakFallbackHandler {
        public void testHandleBodilessFallback(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }

        public <T> T testHandleTypedFallback(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }

    @Test
    void handleBodilessFallback_ShouldRethrowException() {
        TestHandler handler = new TestHandler();
        RuntimeException ex = new RuntimeException("test error");

        assertThrows(RuntimeException.class, () -> handler.testHandleBodilessFallback(ex));
    }

    @Test
    void handleTypedFallback_ShouldRethrowException() {
        TestHandler handler = new TestHandler();
        RuntimeException ex = new RuntimeException("test error");

        assertThrows(RuntimeException.class, () -> handler.testHandleTypedFallback(ex));
    }
}
