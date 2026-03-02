package com.yas.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AbstractCircuitBreakFallbackHandlerTest {

    private TestCircuitBreakFallbackHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TestCircuitBreakFallbackHandler();
    }

    @Test
    void testHandleBodilessFallback_shouldRethrowException() {
        // Given
        RuntimeException exception = new RuntimeException("Circuit breaker error");

        // When/Then
        assertThatThrownBy(() -> handler.handleBodilessFallback(exception))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Circuit breaker error");
    }

    @Test
    void testHandleTypedFallback_shouldReturnNullAndRethrowException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // When/Then
        assertThatThrownBy(() -> handler.handleTypedFallback(exception))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid argument");
    }

    @Test
    void testHandleBodilessFallback_withCustomException_shouldRethrow() {
        // Given
        CustomTestException exception = new CustomTestException("Custom error");

        // When/Then
        assertThatThrownBy(() -> handler.handleBodilessFallback(exception))
                .isInstanceOf(CustomTestException.class)
                .hasMessage("Custom error");
    }

    @Test
    void testHandleTypedFallback_withNullPointerException_shouldRethrow() {
        // Given
        NullPointerException exception = new NullPointerException("Null value encountered");

        // When/Then
        assertThatThrownBy(() -> handler.<String>handleTypedFallback(exception))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Null value encountered");
    }

    @Test
    void testHandleTypedFallback_returnsNullBeforeThrowingException() {
        // Given
        RuntimeException exception = new RuntimeException("Test error");

        // When/Then - The method should attempt to return null but then throw
        assertThatThrownBy(() -> {
            String result = handler.handleTypedFallback(exception);
            // This line should never execute due to exception
            assertThat(result).isNull();
        }).isInstanceOf(RuntimeException.class);
    }

    // Concrete implementation for testing
    private static class TestCircuitBreakFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        // No additional implementation needed for testing
    }

    // Custom exception for testing
    private static class CustomTestException extends Exception {
        public CustomTestException(String message) {
            super(message);
        }
    }
}
