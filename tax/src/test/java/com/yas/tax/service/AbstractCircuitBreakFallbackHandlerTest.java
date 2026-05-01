package com.yas.tax.service;
// Test
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private final TestFallbackHandler handler = new TestFallbackHandler();

    @Test
    void handleBodilessFallback_shouldThrowThrowable() {
        Throwable throwable = new RuntimeException("Test bodiless exception");

        Throwable exception = assertThrows(Throwable.class, () -> {
            handler.testBodilessFallback(throwable);
        });

        assertEquals("Test bodiless exception", exception.getMessage());
    }
    // test
    @Test
    void handleTypedFallback_shouldThrowThrowable() {
        Throwable throwable = new RuntimeException("Test typed exception");

        Throwable exception = assertThrows(Throwable.class, () -> {
            handler.testTypedFallback(throwable);
        });

        assertEquals("Test typed exception", exception.getMessage());
    }

    private static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        public void testBodilessFallback(Throwable t) throws Throwable {
            super.handleBodilessFallback(t);
        }

        public <T> T testTypedFallback(Throwable t) throws Throwable {
            return super.handleTypedFallback(t);
        }
    }
}

// fix error sonacloud //// fix error sonacloud //