package com.yas.payment.model.enumeration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentStatusTest {

    @Test
    void testPaymentStatusValues() {
        PaymentStatus[] statuses = PaymentStatus.values();
        
        assertEquals(3, statuses.length);
        assertEquals(PaymentStatus.PENDING, statuses[0]);
        assertEquals(PaymentStatus.COMPLETED, statuses[1]);
        assertEquals(PaymentStatus.CANCELLED, statuses[2]);
    }

    @Test
    void testPaymentStatusValueOf() {
        assertEquals(PaymentStatus.PENDING, PaymentStatus.valueOf("PENDING"));
        assertEquals(PaymentStatus.COMPLETED, PaymentStatus.valueOf("COMPLETED"));
        assertEquals(PaymentStatus.CANCELLED, PaymentStatus.valueOf("CANCELLED"));
    }

    @Test
    void testPaymentStatusName() {
        assertEquals("PENDING", PaymentStatus.PENDING.name());
        assertEquals("COMPLETED", PaymentStatus.COMPLETED.name());
        assertEquals("CANCELLED", PaymentStatus.CANCELLED.name());
    }
}
