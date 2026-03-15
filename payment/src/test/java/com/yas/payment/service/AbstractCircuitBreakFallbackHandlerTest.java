package com.yas.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private TestFallbackHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TestFallbackHandler();
    }

    @Test
    void testHandleBodilessFallback_shouldRethrowException() {
        RuntimeException ex = new RuntimeException("bodiless error");
        Throwable thrown = assertThrows(RuntimeException.class,
            () -> handler.callBodilessFallback(ex));
        assertEquals("bodiless error", thrown.getMessage());
    }

    @Test
    void testHandleTypedFallback_shouldRethrowException() {
        RuntimeException ex = new RuntimeException("typed error");
        Throwable thrown = assertThrows(RuntimeException.class,
            () -> handler.callTypedFallback(ex));
        assertEquals("typed error", thrown.getMessage());
    }

    // Dummy concrete class to test the abstract handler
    static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        public void callBodilessFallback(Throwable t) throws Throwable {
            handleBodilessFallback(t);
        }
        public <T> T callTypedFallback(Throwable t) throws Throwable {
            return handleTypedFallback(t);
        }
    }
}
