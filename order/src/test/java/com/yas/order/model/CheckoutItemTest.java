package com.yas.order.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CheckoutItemTest {

    @Test
    void testBuilderAndGetters() {
        Checkout checkout = Checkout.builder().id("checkout-1").build();
        CheckoutItem item = CheckoutItem.builder()
                .id(1L)
                .productId(100L)
                .productName("Product A")
                .description("A description")
                .quantity(5)
                .productPrice(BigDecimal.valueOf(19.99))
                .taxAmount(BigDecimal.valueOf(2.00))
                .discountAmount(BigDecimal.valueOf(1.00))
                .shipmentFee(BigDecimal.valueOf(3.00))
                .shipmentTax(BigDecimal.valueOf(0.50))
                .checkout(checkout)
                .build();

        assertEquals(1L, item.getId());
        assertEquals(100L, item.getProductId());
        assertEquals("Product A", item.getProductName());
        assertEquals("A description", item.getDescription());
        assertEquals(5, item.getQuantity());
        assertEquals(BigDecimal.valueOf(19.99), item.getProductPrice());
        assertEquals(BigDecimal.valueOf(2.00), item.getTaxAmount());
        assertEquals(BigDecimal.valueOf(1.00), item.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(3.00), item.getShipmentFee());
        assertEquals(BigDecimal.valueOf(0.50), item.getShipmentTax());
        assertNotNull(item.getCheckout());
    }

    @Test
    void testSetters() {
        CheckoutItem item = new CheckoutItem();
        item.setId(2L);
        item.setProductId(200L);
        item.setProductName("Product B");
        item.setQuantity(10);
        item.setProductPrice(BigDecimal.TEN);

        assertEquals(2L, item.getId());
        assertEquals(200L, item.getProductId());
        assertEquals("Product B", item.getProductName());
        assertEquals(10, item.getQuantity());
        assertEquals(BigDecimal.TEN, item.getProductPrice());
    }

    @Test
    void testToBuilder() {
        CheckoutItem item = CheckoutItem.builder()
                .id(1L)
                .productId(100L)
                .productName("Original")
                .quantity(1)
                .build();

        CheckoutItem modified = item.toBuilder()
                .productName("Modified")
                .quantity(5)
                .build();

        assertEquals("Modified", modified.getProductName());
        assertEquals(5, modified.getQuantity());
        assertEquals(1L, modified.getId());
    }

    @Test
    void testEquals_SameObject() {
        CheckoutItem item = CheckoutItem.builder().id(1L).build();
        assertTrue(item.equals(item));
    }

    @Test
    void testEquals_DifferentType() {
        CheckoutItem item = CheckoutItem.builder().id(1L).build();
        assertFalse(item.equals("not a checkout item"));
    }

    @Test
    void testEquals_NullId() {
        CheckoutItem item1 = CheckoutItem.builder().id(null).build();
        CheckoutItem item2 = CheckoutItem.builder().id(1L).build();
        assertFalse(item1.equals(item2));
    }

    @Test
    void testEquals_SameId() {
        CheckoutItem item1 = CheckoutItem.builder().id(1L).build();
        CheckoutItem item2 = CheckoutItem.builder().id(1L).build();
        assertTrue(item1.equals(item2));
    }

    @Test
    void testEquals_DifferentId() {
        CheckoutItem item1 = CheckoutItem.builder().id(1L).build();
        CheckoutItem item2 = CheckoutItem.builder().id(2L).build();
        assertFalse(item1.equals(item2));
    }

    @Test
    void testHashCode() {
        CheckoutItem item1 = CheckoutItem.builder().id(1L).build();
        CheckoutItem item2 = CheckoutItem.builder().id(2L).build();
        // hashCode is class-based, not id-based
        assertEquals(item1.hashCode(), item2.hashCode());
    }
}
