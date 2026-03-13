package com.yas.order.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void testOrderItemBuilder_shouldCreateValidOrderItem() {
        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .productId(100L)
                .orderId(10L)
                .productName("Test Product")
                .quantity(2)
                .productPrice(new BigDecimal("29.99"))
                .note("Test note")
                .discountAmount(new BigDecimal("5.00"))
                .taxAmount(new BigDecimal("2.40"))
                .taxPercent(new BigDecimal("8.00"))
                .build();

        assertNotNull(orderItem);
        assertEquals(1L, orderItem.getId());
        assertEquals(100L, orderItem.getProductId());
        assertEquals(10L, orderItem.getOrderId());
        assertEquals("Test Product", orderItem.getProductName());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(new BigDecimal("29.99"), orderItem.getProductPrice());
        assertEquals("Test note", orderItem.getNote());
        assertEquals(new BigDecimal("5.00"), orderItem.getDiscountAmount());
        assertEquals(new BigDecimal("2.40"), orderItem.getTaxAmount());
        assertEquals(new BigDecimal("8.00"), orderItem.getTaxPercent());
    }

    @Test
    void testOrderItemNoArgsConstructor_shouldCreateEmptyOrderItem() {
        OrderItem orderItem = new OrderItem();

        assertNotNull(orderItem);
        assertNull(orderItem.getId());
        assertNull(orderItem.getProductId());
        assertNull(orderItem.getProductName());
    }

    @Test
    void testOrderItemSetters_shouldUpdateFields() {
        OrderItem orderItem = new OrderItem();

        orderItem.setId(5L);
        orderItem.setProductId(200L);
        orderItem.setOrderId(20L);
        orderItem.setProductName("Updated Product");
        orderItem.setQuantity(3);
        orderItem.setProductPrice(new BigDecimal("49.99"));
        orderItem.setNote("Updated note");

        assertEquals(5L, orderItem.getId());
        assertEquals(200L, orderItem.getProductId());
        assertEquals(20L, orderItem.getOrderId());
        assertEquals("Updated Product", orderItem.getProductName());
        assertEquals(3, orderItem.getQuantity());
        assertEquals(new BigDecimal("49.99"), orderItem.getProductPrice());
        assertEquals("Updated note", orderItem.getNote());
    }
}
