package com.yas.customer.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        void callHandleError(Throwable throwable) throws Throwable {
            handleError(throwable);
        }

        void callHandleBodilessFallback(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }

        Object callHandleTypedFallback(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }

    private static class SilentFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        @Override
        protected void handleError(Throwable throwable) {
        }

        Object callHandleTypedFallback(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }

        void callHandleBodilessFallback(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }
    }

    private final TestFallbackHandler fallbackHandler = new TestFallbackHandler();
    private final SilentFallbackHandler silentFallbackHandler = new SilentFallbackHandler();

    @Test
    void handleError_ShouldRethrowThrowable() {
        RuntimeException expected = new RuntimeException("error");

        RuntimeException actual = assertThrows(RuntimeException.class,
                () -> fallbackHandler.callHandleError(expected));

        assertSame(expected, actual);
    }

    @Test
    void handleBodilessFallback_ShouldRethrowThrowable() {
        RuntimeException expected = new RuntimeException("error");

        RuntimeException actual = assertThrows(RuntimeException.class,
                () -> fallbackHandler.callHandleBodilessFallback(expected));

        assertSame(expected, actual);
    }

    @Test
    void handleTypedFallback_ShouldRethrowThrowable() {
        RuntimeException expected = new RuntimeException("error");

        RuntimeException actual = assertThrows(RuntimeException.class,
                () -> fallbackHandler.callHandleTypedFallback(expected));

        assertSame(expected, actual);
    }

    @Test
    void handleTypedFallback_WhenHandleErrorDoesNotThrow_ShouldReturnNull() throws Throwable {
        Object actual = silentFallbackHandler.callHandleTypedFallback(new RuntimeException("ignored"));

        assertNull(actual);
    }

    @Test
    void handleBodilessFallback_WhenHandleErrorDoesNotThrow_ShouldComplete() throws Throwable {
        silentFallbackHandler.callHandleBodilessFallback(new RuntimeException("ignored"));
    }
}
