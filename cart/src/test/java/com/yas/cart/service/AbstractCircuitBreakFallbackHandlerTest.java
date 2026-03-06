package com.yas.cart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractCircuitBreakFallbackHandlerTest {

    private TestCircuitBreakFallbackHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TestCircuitBreakFallbackHandler();
    }

    @Test
    void testHandleBodilessFallback_shouldRethrowException() {
        // Given
        RuntimeException exception = new RuntimeException("Test error");

        // When & Then
        RuntimeException thrown = assertThrows(RuntimeException.class,
            () -> handler.handleBodilessFallback(exception));
        
        assertEquals("Test error", thrown.getMessage());
        assertSame(exception, thrown);
    }

    @Test
    void testHandleTypedFallback_shouldRethrowException() {
        // Given
        IllegalStateException exception = new IllegalStateException("State error");

        // When & Then
        IllegalStateException thrown = assertThrows(IllegalStateException.class,
            () -> handler.handleTypedFallback(exception));
        
        assertEquals("State error", thrown.getMessage());
        assertSame(exception, thrown);
    }

    @Test
    void testHandleTypedFallback_withIOException_shouldRethrowException() throws Throwable {
        // Given
        Exception exception = new Exception("IO error");

        // When & Then
        Exception thrown = assertThrows(Exception.class,
            () -> handler.handleTypedFallback(exception));
        
        assertEquals("IO error", thrown.getMessage());
        assertSame(exception, thrown);
    }

    // Helper class to test abstract class
    private static class TestCircuitBreakFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        // Expose protected methods for testing
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
