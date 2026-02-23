package com.yas.rating.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AbstractCircuitBreakFallbackHandlerTest {

    private static class TestCircuitBreakHandler extends AbstractCircuitBreakFallbackHandler {
        @Override
        protected Object handleFallback(Throwable throwable) throws Throwable {
            return super.handleFallback(throwable);
        }

        public void testHandleBodilessFallback(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }
    }

    @Test
    void testHandleFallback_shouldThrowException() {
        // Given
        TestCircuitBreakHandler handler = new TestCircuitBreakHandler();
        RuntimeException exception = new RuntimeException("Test exception");

        // When & Then
        assertThatThrownBy(() -> handler.handleFallback(exception))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Test exception");
    }

    @Test
    void testHandleBodilessFallback_shouldThrowException() {
        // Given
        TestCircuitBreakHandler handler = new TestCircuitBreakHandler();
        RuntimeException exception = new RuntimeException("Bodiless exception");

        // When & Then
        assertThatThrownBy(() -> handler.testHandleBodilessFallback(exception))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bodiless exception");
    }

    @Test
    void testHandleFallback_whenNullPointerException_shouldThrowNullPointerException() {
        // Given
        TestCircuitBreakHandler handler = new TestCircuitBreakHandler();
        NullPointerException exception = new NullPointerException("Null value");

        // When & Then
        assertThatThrownBy(() -> handler.handleFallback(exception))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Null value");
    }

    @Test
    void testHandleBodilessFallback_whenIllegalArgumentException_shouldThrowIllegalArgumentException() {
        // Given
        TestCircuitBreakHandler handler = new TestCircuitBreakHandler();
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // When & Then
        assertThatThrownBy(() -> handler.testHandleBodilessFallback(exception))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid argument");
    }
}
