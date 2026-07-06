package com.yas.payment.paypal.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CircuitBreakFallbackHandlerTest {

    private final TestFallbackHandler handler = new TestFallbackHandler();

    @Test
    void handleBodilessFallbackRethrowsOriginalError() {
        RuntimeException error = new RuntimeException("boom");

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> handler.handleBodilessFallback(error));

        assertSame(error, thrown);
    }

    @Test
    void handleTypedFallbackRethrowsOriginalError() {
        RuntimeException error = new RuntimeException("boom");

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> handler.handleTypedFallback(error));

        assertSame(error, thrown);
    }

    private static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
    }
}
