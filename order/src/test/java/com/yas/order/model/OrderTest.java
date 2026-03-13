package com.yas.order.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void testOrderBuilder_shouldCreateValidOrder() {
        OrderAddress shippingAddress = OrderAddress.builder().id(1L).contactName("John").build();
        OrderAddress billingAddress = OrderAddress.builder().id(2L).contactName("Jane").build();

        Order order = Order.builder()
                .id(1L)
                .email("test@example.com")
                .note("Test note")
                .tax(5.0f)
                .discount(10.0f)
                .numberItem(3)
                .totalPrice(new BigDecimal("100.00"))
                .deliveryFee(new BigDecimal("5.00"))
                .couponCode("COUPON")
                .orderStatus(OrderStatus.PENDING)
                .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .paymentStatus(PaymentStatus.PENDING)
                .shippingAddressId(shippingAddress)
                .billingAddressId(billingAddress)
                .checkoutId("checkout-1")
                .build();

        assertNotNull(order);
        assertEquals(1L, order.getId());
        assertEquals("test@example.com", order.getEmail());
        assertEquals("Test note", order.getNote());
        assertEquals(5.0f, order.getTax());
        assertEquals(10.0f, order.getDiscount());
        assertEquals(3, order.getNumberItem());
        assertEquals(new BigDecimal("100.00"), order.getTotalPrice());
        assertEquals(new BigDecimal("5.00"), order.getDeliveryFee());
        assertEquals("COUPON", order.getCouponCode());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        assertEquals(DeliveryMethod.YAS_EXPRESS, order.getDeliveryMethod());
        assertEquals(DeliveryStatus.PREPARING, order.getDeliveryStatus());
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        assertEquals(shippingAddress, order.getShippingAddressId());
        assertEquals(billingAddress, order.getBillingAddressId());
        assertEquals("checkout-1", order.getCheckoutId());
    }

    @Test
    void testOrderNoArgsConstructor_shouldCreateEmptyOrder() {
        Order order = new Order();

        assertNotNull(order);
        assertNull(order.getId());
        assertNull(order.getEmail());
    }

    @Test
    void testOrderSetters_shouldUpdateFields() {
        Order order = new Order();

        order.setId(1L);
        order.setEmail("updated@example.com");
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setPaymentStatus(PaymentStatus.COMPLETED);
        order.setPaymentId(100L);
        order.setRejectReason("Out of stock");

        assertEquals(1L, order.getId());
        assertEquals("updated@example.com", order.getEmail());
        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());
        assertEquals(PaymentStatus.COMPLETED, order.getPaymentStatus());
        assertEquals(100L, order.getPaymentId());
        assertEquals("Out of stock", order.getRejectReason());
    }

    @Test
    void testOrderBuilder_withAllEnumValues_shouldSetCorrectly() {
        for (OrderStatus status : OrderStatus.values()) {
            Order order = Order.builder().orderStatus(status).build();
            assertEquals(status, order.getOrderStatus());
        }
    }
}
