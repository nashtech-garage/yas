package com.yas.promotion.service;

import java.util.List;
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
        List<Long> ids = List.of(1L, 2L);
        Throwable thrown = assertThrows(Throwable.class, () -> fallbackHandler.executeBodiless(ids, t));
        assertEquals("Test bodiless exception", thrown.getMessage());
    }

    @Test
    void testHandleFallback() {
        Throwable t = new RuntimeException("Test fallback exception");
        List<Long> ids = List.of(3L);
        Throwable thrown = assertThrows(Throwable.class, () -> fallbackHandler.executeFallback(ids, t));
        assertEquals("Test fallback exception", thrown.getMessage());
    }

    // Dummy concrete class to test abstract fallback methods
    static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        public void executeBodiless(List<Long> ids, Throwable t) throws Throwable {
            handleBodilessFallback(ids, t);
        }

        public Object executeFallback(List<Long> ids, Throwable t) throws Throwable {
            return handleFallback(ids, t);
        }
    }
}
