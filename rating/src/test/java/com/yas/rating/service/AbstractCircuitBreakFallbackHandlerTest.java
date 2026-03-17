package com.yas.rating.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {

        Object invokeHandleFallback(Throwable throwable) throws Throwable {
            return handleFallback(throwable);
        }

        void invokeHandleBodilessFallback(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }
    }

    private final TestFallbackHandler fallbackHandler = new TestFallbackHandler();

    @Test
    void handleFallback_ShouldRethrowThrowable() {
        RuntimeException expected = new RuntimeException("fallback failed");

        RuntimeException actual = assertThrows(RuntimeException.class,
                () -> fallbackHandler.invokeHandleFallback(expected));

        assertSame(expected, actual);
    }

    @Test
    void handleBodilessFallback_ShouldRethrowThrowable() {
        RuntimeException expected = new RuntimeException("fallback failed");

        RuntimeException actual = assertThrows(RuntimeException.class,
                () -> fallbackHandler.invokeHandleBodilessFallback(expected));

        assertSame(expected, actual);
    }
}
