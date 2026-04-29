package com.yas.customer.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private static final class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        void callBodiless(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }

        void callError(Throwable throwable) throws Throwable {
            handleError(throwable);
        }

        <T> T callTyped(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }

    @Test
    void testHandleBodilessFallback_throwsOriginalException() {
        TestFallbackHandler handler = new TestFallbackHandler();
        RuntimeException exception = new RuntimeException("boom");

        assertThrows(RuntimeException.class, () -> handler.callBodiless(exception));
    }

    @Test
    void testHandleError_throwsOriginalException() {
        TestFallbackHandler handler = new TestFallbackHandler();
        RuntimeException exception = new RuntimeException("boom");

        assertThrows(RuntimeException.class, () -> handler.callError(exception));
    }

    @Test
    void testHandleTypedFallback_throwsOriginalException() {
        TestFallbackHandler handler = new TestFallbackHandler();
        RuntimeException exception = new RuntimeException("boom");

        assertThrows(RuntimeException.class, () -> handler.callTyped(exception));
    }
}
