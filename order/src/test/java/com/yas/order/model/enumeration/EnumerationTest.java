package com.yas.order.model.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class EnumerationTest {

    @Test
    void testCheckoutStateValues() {
        CheckoutState[] values = CheckoutState.values();
        assertEquals(8, values.length);
    }

    @Test
    void testCheckoutStateGetName() {
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
    void testCheckoutStateValueOf() {
        assertEquals(CheckoutState.COMPLETED, CheckoutState.valueOf("COMPLETED"));
        assertEquals(CheckoutState.PENDING, CheckoutState.valueOf("PENDING"));
        assertEquals(CheckoutState.LOCK, CheckoutState.valueOf("LOCK"));
    }

    @Test
    void testOrderStatusValues() {
        OrderStatus[] values = OrderStatus.values();
        assertEquals(9, values.length);
    }

    @Test
    void testOrderStatusGetName() {
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
    void testOrderStatusValueOf() {
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(OrderStatus.COMPLETED, OrderStatus.valueOf("COMPLETED"));
        assertEquals(OrderStatus.REJECT, OrderStatus.valueOf("REJECT"));
    }

    @Test
    void testDeliveryMethodValues() {
        DeliveryMethod[] values = DeliveryMethod.values();
        assertEquals(4, values.length);
        assertNotNull(DeliveryMethod.valueOf("VIETTEL_POST"));
        assertNotNull(DeliveryMethod.valueOf("GRAB_EXPRESS"));
        assertNotNull(DeliveryMethod.valueOf("SHOPEE_EXPRESS"));
        assertNotNull(DeliveryMethod.valueOf("YAS_EXPRESS"));
    }

    @Test
    void testDeliveryStatusValues() {
        DeliveryStatus[] values = DeliveryStatus.values();
        assertEquals(4, values.length);
        assertNotNull(DeliveryStatus.valueOf("PREPARING"));
        assertNotNull(DeliveryStatus.valueOf("DELIVERING"));
        assertNotNull(DeliveryStatus.valueOf("DELIVERED"));
        assertNotNull(DeliveryStatus.valueOf("CANCELLED"));
    }

    @Test
    void testPaymentMethodValues() {
        PaymentMethod[] values = PaymentMethod.values();
        assertEquals(3, values.length);
        assertNotNull(PaymentMethod.valueOf("COD"));
        assertNotNull(PaymentMethod.valueOf("BANKING"));
        assertNotNull(PaymentMethod.valueOf("PAYPAL"));
    }

    @Test
    void testPaymentStatusValues() {
        PaymentStatus[] values = PaymentStatus.values();
        assertEquals(3, values.length);
        assertNotNull(PaymentStatus.valueOf("PENDING"));
        assertNotNull(PaymentStatus.valueOf("COMPLETED"));
        assertNotNull(PaymentStatus.valueOf("CANCELLED"));
    }
}
