package com.yas.cart.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    /**
     * Concrete subclass used for testing the abstract handler.
     */
    private static class TestHandler extends AbstractCircuitBreakFallbackHandler {
        public void callHandleBodilessFallback(Throwable t) throws Throwable {
            handleBodilessFallback(t);
        }

        public <T> T callHandleTypedFallback(Throwable t) throws Throwable {
            return handleTypedFallback(t);
        }
    }

    private TestHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TestHandler();
    }

    @Nested
    class HandleBodilessFallbackTest {

        @Test
        void handleBodilessFallback_whenRuntimeExceptionProvided_shouldRethrow() {
            RuntimeException cause = new RuntimeException("service unavailable");

            Throwable thrown = assertThrows(RuntimeException.class,
                () -> handler.callHandleBodilessFallback(cause));

            assertTrue(thrown.getMessage().contains("service unavailable"));
        }

        @Test
        void handleBodilessFallback_whenCheckedExceptionProvided_shouldRethrow() {
            Exception cause = new Exception("checked error");

            Throwable thrown = assertThrows(Exception.class,
                () -> handler.callHandleBodilessFallback(cause));

            assertTrue(thrown.getMessage().contains("checked error"));
        }

        @Test
        void handleBodilessFallback_whenIllegalStateException_shouldRethrowSameException() {
            IllegalStateException cause = new IllegalStateException("illegal state");

            Throwable thrown = assertThrows(IllegalStateException.class,
                () -> handler.callHandleBodilessFallback(cause));

            assertTrue(thrown.getMessage().contains("illegal state"));
        }
    }

    @Nested
    class HandleTypedFallbackTest {

        @Test
        void handleTypedFallback_whenRuntimeExceptionProvided_shouldRethrow() {
            RuntimeException cause = new RuntimeException("typed fallback error");

            Throwable thrown = assertThrows(RuntimeException.class,
                () -> handler.callHandleTypedFallback(cause));

            assertTrue(thrown.getMessage().contains("typed fallback error"));
        }

        @Test
        void handleTypedFallback_whenCheckedExceptionProvided_shouldRethrow() {
            Exception cause = new Exception("typed checked error");

            Throwable thrown = assertThrows(Exception.class,
                () -> handler.callHandleTypedFallback(cause));

            assertTrue(thrown.getMessage().contains("typed checked error"));
        }

        @Test
        void handleTypedFallback_whenIllegalArgumentException_shouldRethrowSameType() {
            IllegalArgumentException cause = new IllegalArgumentException("bad argument");

            Throwable thrown = assertThrows(IllegalArgumentException.class,
                () -> handler.callHandleTypedFallback(cause));

            assertTrue(thrown.getMessage().contains("bad argument"));
        }
    }
}
