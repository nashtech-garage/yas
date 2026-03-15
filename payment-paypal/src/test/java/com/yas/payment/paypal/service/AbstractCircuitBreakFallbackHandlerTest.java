package com.yas.payment.paypal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractCircuitBreakFallbackHandlerTest {

    private TestFallbackHandler fallbackHandler;

    @BeforeEach
    void setUp() {
        fallbackHandler = new TestFallbackHandler();
    }

    @Test
    void testHandleBodilessFallback() {
        Throwable t = new RuntimeException("Test bodiless exception");
        Throwable thrown = assertThrows(Throwable.class, () -> fallbackHandler.executeBodiless(t));
        assertEquals("Test bodiless exception", thrown.getMessage());
    }

    @Test
    void testHandleTypedFallback() {
        Throwable t = new RuntimeException("Test typed exception");
        Throwable thrown = assertThrows(Throwable.class, () -> fallbackHandler.executeTyped(t));
        assertEquals("Test typed exception", thrown.getMessage());
    }

    // Dummy concrete class to test abstract fallback methods
    static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        public void executeBodiless(Throwable t) throws Throwable {
            handleBodilessFallback(t);
        }

        public String executeTyped(Throwable t) throws Throwable {
            return handleTypedFallback(t);
        }
    }
}
