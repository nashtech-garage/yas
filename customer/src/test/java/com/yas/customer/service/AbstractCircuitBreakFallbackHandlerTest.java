package com.yas.customer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractCircuitBreakFallbackHandlerTest {

    private TestCircuitBreakFallbackHandler handler;

    // Concrete implementation for testing the abstract class
    static class TestCircuitBreakFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        // Test implementation that exposes protected methods
        public void testHandleBodilessFallback(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }

        public void testHandleError(Throwable throwable) throws Throwable {
            handleError(throwable);
        }

        public <T> T testHandleTypedFallback(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }

    @BeforeEach
    void setUp() {
        handler = new TestCircuitBreakFallbackHandler();
    }

    @Test
    void testHandleBodilessFallback_shouldThrowOriginalException() {
        RuntimeException originalException = new RuntimeException("Test exception");

        Throwable thrown = assertThrows(RuntimeException.class,
                () -> handler.testHandleBodilessFallback(originalException));

        assertEquals(originalException, thrown);
        assertEquals("Test exception", thrown.getMessage());
    }

    @Test
    void testHandleError_shouldThrowOriginalException() {
        IllegalArgumentException originalException = new IllegalArgumentException("Invalid argument");

        Throwable thrown = assertThrows(IllegalArgumentException.class,
                () -> handler.testHandleError(originalException));

        assertEquals(originalException, thrown);
        assertEquals("Invalid argument", thrown.getMessage());
    }

    @Test
    void testHandleTypedFallback_shouldThrowOriginalException() {
        NullPointerException originalException = new NullPointerException("Null value encountered");

        Throwable thrown = assertThrows(NullPointerException.class,
                () -> handler.testHandleTypedFallback(originalException));

        assertEquals(originalException, thrown);
        assertEquals("Null value encountered", thrown.getMessage());
    }

    @Test
    void testHandleBodilessFallback_withCheckedException() {
        Exception checkedException = new Exception("Checked exception");

        Throwable thrown = assertThrows(Exception.class,
                () -> handler.testHandleBodilessFallback(checkedException));

        assertEquals(checkedException, thrown);
        assertEquals("Checked exception", thrown.getMessage());
    }

    @Test
    void testHandleTypedFallback_withDifferentExceptionTypes() {
        // Test with different exception types
        IllegalStateException stateException = new IllegalStateException("Invalid state");

        Throwable thrown = assertThrows(IllegalStateException.class,
                () -> handler.<String>testHandleTypedFallback(stateException));

        assertEquals(stateException, thrown);
    }

    @Test
    void testHandleError_withNestedCause() {
        RuntimeException cause = new RuntimeException("Root cause");
        RuntimeException exception = new RuntimeException("Wrapper exception", cause);

        Throwable thrown = assertThrows(RuntimeException.class,
                () -> handler.testHandleError(exception));

        assertEquals(exception, thrown);
        assertEquals("Wrapper exception", thrown.getMessage());
        assertEquals(cause, thrown.getCause());
    }
}
