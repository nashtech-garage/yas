package com.yas.cart.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private ConcreteCircuitBreakFallbackHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ConcreteCircuitBreakFallbackHandler();
    }

    static class ConcreteCircuitBreakFallbackHandler extends AbstractCircuitBreakFallbackHandler {
    }

    @Nested
    class HandleBodilessFallbackTest {

        @Test
        void testHandleBodilessFallback_whenRuntimeExceptionThrown_shouldRethrow() {
            RuntimeException exception = new RuntimeException("circuit breaker error");
            Throwable thrown = assertThrows(RuntimeException.class,
                () -> handler.handleBodilessFallback(exception));
            assertSame(exception, thrown);
        }

        @Test
        void testHandleBodilessFallback_whenCheckedExceptionThrown_shouldRethrow() {
            Exception exception = new Exception("checked exception");
            Throwable thrown = assertThrows(Exception.class,
                () -> handler.handleBodilessFallback(exception));
            assertSame(exception, thrown);
        }

        @Test
        void testHandleBodilessFallback_whenErrorThrown_shouldRethrow() {
            Error error = new Error("system error");
            Throwable thrown = assertThrows(Error.class,
                () -> handler.handleBodilessFallback(error));
            assertSame(error, thrown);
        }
    }

    @Nested
    class HandleTypedFallbackTest {

        @Test
        void testHandleTypedFallback_whenRuntimeExceptionThrown_shouldRethrow() {
            RuntimeException exception = new RuntimeException("typed fallback error");
            Throwable thrown = assertThrows(RuntimeException.class,
                () -> handler.handleTypedFallback(exception));
            assertSame(exception, thrown);
        }

        @Test
        void testHandleTypedFallback_whenCheckedExceptionThrown_shouldRethrow() {
            Exception exception = new Exception("typed checked exception");
            Throwable thrown = assertThrows(Exception.class,
                () -> handler.handleTypedFallback(exception));
            assertSame(exception, thrown);
        }

        @Test
        void testHandleTypedFallback_whenNullMessageException_shouldRethrowWithNullMessage() {
            RuntimeException exception = new RuntimeException((String) null);
            Throwable thrown = assertThrows(RuntimeException.class,
                () -> handler.handleTypedFallback(exception));
            assertNull(thrown.getMessage());
        }
    }
}
