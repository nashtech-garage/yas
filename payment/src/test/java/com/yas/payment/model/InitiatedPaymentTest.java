package com.yas.payment.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InitiatedPaymentTest {

    @Test
    void testInitiatedPaymentBuilder() {
        // Given & When
        InitiatedPayment payment = InitiatedPayment.builder()
                .status("SUCCESS")
                .paymentId("pay_123")
                .redirectUrl("https://example.com/payment")
                .build();

        // Then
        assertNotNull(payment);
        assertEquals("SUCCESS", payment.getStatus());
        assertEquals("pay_123", payment.getPaymentId());
        assertEquals("https://example.com/payment", payment.getRedirectUrl());
    }

    @Test
    void testInitiatedPaymentGettersAndSetters() {
        // Given
        InitiatedPayment payment = InitiatedPayment.builder().build();

        // When
        payment.setStatus("PENDING");
        payment.setPaymentId("pay_456");
        payment.setRedirectUrl("https://example.com/redirect");

        // Then
        assertEquals("PENDING", payment.getStatus());
        assertEquals("pay_456", payment.getPaymentId());
        assertEquals("https://example.com/redirect", payment.getRedirectUrl());
    }

    @Test
    void testInitiatedPaymentAllArgsConstructor() {
        // When
        InitiatedPayment payment = new InitiatedPayment(
                "COMPLETED",
                "pay_789",
                "https://example.com/success"
        );

        // Then
        assertNotNull(payment);
        assertEquals("COMPLETED", payment.getStatus());
        assertEquals("pay_789", payment.getPaymentId());
        assertEquals("https://example.com/success", payment.getRedirectUrl());
    }

    @Test
    void testInitiatedPaymentWithNullValues() {
        // When
        InitiatedPayment payment = InitiatedPayment.builder()
                .status(null)
                .paymentId(null)
                .redirectUrl(null)
                .build();

        // Then
        assertNotNull(payment);
        assertNull(payment.getStatus());
        assertNull(payment.getPaymentId());
        assertNull(payment.getRedirectUrl());
    }
}
