package com.yas.order.model.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CheckoutStateTest {

    @Test
    void testAllValues_shouldExist() {
        CheckoutState[] values = CheckoutState.values();
        assertEquals(8, values.length);
    }

    @Test
    void testGetName_shouldReturnCorrectName() {
        assertEquals("Completed", CheckoutState.COMPLETED.getName());
        assertEquals("Pending", CheckoutState.PENDING.getName());
        assertEquals("LOCK", CheckoutState.LOCK.getName());
        assertEquals("Checked Out", CheckoutState.CHECKED_OUT.getName());
        assertEquals("Payment Processing", CheckoutState.PAYMENT_PROCESSING.getName());
        assertEquals("Payment Failed", CheckoutState.PAYMENT_FAILED.getName());
        assertEquals("Payment Confirmed", CheckoutState.PAYMENT_CONFIRMED.getName());
        assertEquals("Fulfilled", CheckoutState.FULFILLED.getName());
    }

    @Test
    void testValueOf_shouldReturnCorrectEnum() {
        assertEquals(CheckoutState.PENDING, CheckoutState.valueOf("PENDING"));
        assertEquals(CheckoutState.COMPLETED, CheckoutState.valueOf("COMPLETED"));
    }
}
