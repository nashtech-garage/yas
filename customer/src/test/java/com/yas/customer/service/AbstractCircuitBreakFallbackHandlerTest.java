package com.yas.customer.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractCircuitBreakFallbackHandlerTest {

    private final AbstractCircuitBreakFallbackHandler handler = new AbstractCircuitBreakFallbackHandler() {
    };

    @Test
    void testHandleBodilessFallback() {
        Throwable exception = new RuntimeException("Test Exception");
        Throwable thrown = assertThrows(Throwable.class, () -> handler.handleBodilessFallback(exception));
        assertEquals(exception, thrown);
    }

    @Test
    void testHandleError() {
        Throwable exception = new RuntimeException("Test Exception");
        Throwable thrown = assertThrows(Throwable.class, () -> handler.handleError(exception));
        assertEquals(exception, thrown);
    }

    @Test
    void testHandleTypedFallback() {
        Throwable exception = new RuntimeException("Test Exception");
        Throwable thrown = assertThrows(Throwable.class, () -> handler.handleTypedFallback(exception));
        assertEquals(exception, thrown);
    }
}
