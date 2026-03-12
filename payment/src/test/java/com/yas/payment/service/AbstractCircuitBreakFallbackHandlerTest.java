package com.yas.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private AbstractCircuitBreakFallbackHandler handler;

    @BeforeEach
    void setUp() {
        // Tạo một class nặc danh (anonymous) kế thừa class abstract để test
        handler = new AbstractCircuitBreakFallbackHandler() {};
    }

    @Test
    void handleBodilessFallback_shouldThrowException() {
        Throwable t = new RuntimeException("Test exception bodiless");
        
        Throwable thrown = assertThrows(Throwable.class, () -> {
            handler.handleBodilessFallback(t);
        });
        
        assertEquals("Test exception bodiless", thrown.getMessage());
    }

    @Test
    void handleTypedFallback_shouldThrowException() {
        Throwable t = new RuntimeException("Test exception typed");
        
        Throwable thrown = assertThrows(Throwable.class, () -> {
            handler.handleTypedFallback(t);
        });
        
        assertEquals("Test exception typed", thrown.getMessage());
    }
}