package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private TestCircuitBreakFallbackHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TestCircuitBreakFallbackHandler();
    }

    @Test
    void handleBodilessFallback_shouldRethrowException() {
        RuntimeException exception = new RuntimeException("Test error");

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> handler.handleBodilessFallback(exception));

        assertEquals("Test error", thrown.getMessage());
        assertSame(exception, thrown);
    }

    @Test
    void handleTypedFallback_shouldRethrowException() {
        IllegalStateException exception = new IllegalStateException("State error");

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> handler.handleTypedFallback(exception));

        assertEquals("State error", thrown.getMessage());
        assertSame(exception, thrown);
    }

    @Test
    void handleTypedFallback_withCheckedException_shouldRethrowException() throws Throwable {
        Exception exception = new Exception("Checked error");

        Exception thrown = assertThrows(Exception.class,
                () -> handler.handleTypedFallback(exception));

        assertEquals("Checked error", thrown.getMessage());
        assertSame(exception, thrown);
    }

    // Concrete subclass to test the abstract class
    private static class TestCircuitBreakFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        @Override
        public void handleBodilessFallback(Throwable throwable) throws Throwable {
            super.handleBodilessFallback(throwable);
        }

        @Override
        public <T> T handleTypedFallback(Throwable throwable) throws Throwable {
            return super.handleTypedFallback(throwable);
        }
    }
}
