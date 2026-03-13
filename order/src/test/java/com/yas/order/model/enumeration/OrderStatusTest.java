package com.yas.order.model.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class OrderStatusTest {

    @Test
    void testAllValues_shouldExist() {
        OrderStatus[] values = OrderStatus.values();
        assertEquals(9, values.length);
    }

    @Test
    void testGetName_shouldReturnCorrectName() {
        assertEquals("PENDING", OrderStatus.PENDING.getName());
        assertEquals("ACCEPTED", OrderStatus.ACCEPTED.getName());
        assertEquals("PENDING_PAYMENT", OrderStatus.PENDING_PAYMENT.getName());
        assertEquals("PAID", OrderStatus.PAID.getName());
        assertEquals("SHIPPING", OrderStatus.SHIPPING.getName());
        assertEquals("COMPLETED", OrderStatus.COMPLETED.getName());
        assertEquals("REFUND", OrderStatus.REFUND.getName());
        assertEquals("CANCELLED", OrderStatus.CANCELLED.getName());
        assertEquals("REJECT", OrderStatus.REJECT.getName());
    }

    @Test
    void testValueOf_shouldReturnCorrectEnum() {
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(OrderStatus.COMPLETED, OrderStatus.valueOf("COMPLETED"));
        assertEquals(OrderStatus.REJECT, OrderStatus.valueOf("REJECT"));
    }
}
