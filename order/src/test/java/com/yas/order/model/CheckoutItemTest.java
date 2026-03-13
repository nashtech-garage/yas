package com.yas.order.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CheckoutItemTest {

    @Test
    void testBuilder_shouldCreateValidCheckoutItem() {
        CheckoutItem item = CheckoutItem.builder()
                .id(1L)
                .productId(100L)
                .productName("Test Product")
                .quantity(2)
                .productPrice(new BigDecimal("29.99"))
                .taxAmount(new BigDecimal("2.40"))
                .discountAmount(new BigDecimal("5.00"))
                .build();

        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals(100L, item.getProductId());
        assertEquals("Test Product", item.getProductName());
        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("29.99"), item.getProductPrice());
        assertEquals(new BigDecimal("2.40"), item.getTaxAmount());
        assertEquals(new BigDecimal("5.00"), item.getDiscountAmount());
    }

    @Test
    void testNoArgsConstructor_shouldCreateItemWithNullFields() {
        CheckoutItem item = new CheckoutItem();

        assertNotNull(item);
        assertNull(item.getId());
        assertNull(item.getProductId());
        assertNull(item.getProductName());
    }

    @Test
    void testEquals_whenSameInstance_returnsTrue() {
        CheckoutItem item = CheckoutItem.builder().id(1L).build();

        assertTrue(item.equals(item));
    }

    @Test
    void testEquals_whenNull_returnsFalse() {
        CheckoutItem item = CheckoutItem.builder().id(1L).build();

        assertFalse(item.equals(null));
    }

    @Test
    void testEquals_whenDifferentClass_returnsFalse() {
        CheckoutItem item = CheckoutItem.builder().id(1L).build();

        assertFalse(item.equals("not a CheckoutItem"));
    }

    @Test
    void testEquals_whenSameId_returnsTrue() {
        CheckoutItem item1 = CheckoutItem.builder().id(1L).build();
        CheckoutItem item2 = CheckoutItem.builder().id(1L).build();

        assertTrue(item1.equals(item2));
    }

    @Test
    void testEquals_whenDifferentIds_returnsFalse() {
        CheckoutItem item1 = CheckoutItem.builder().id(1L).build();
        CheckoutItem item2 = CheckoutItem.builder().id(2L).build();

        assertFalse(item1.equals(item2));
    }

    @Test
    void testEquals_whenNullId_returnsFalse() {
        CheckoutItem itemWithNullId = new CheckoutItem();
        CheckoutItem itemWithId = CheckoutItem.builder().id(1L).build();

        assertFalse(itemWithNullId.equals(itemWithId));
    }

    @Test
    void testHashCode_shouldReturnClassHashCode() {
        CheckoutItem item = CheckoutItem.builder().id(1L).build();

        assertEquals(CheckoutItem.class.hashCode(), item.hashCode());
    }

    @Test
    void testSetters_shouldUpdateFields() {
        CheckoutItem item = new CheckoutItem();
        item.setId(5L);
        item.setProductId(200L);
        item.setProductName("Updated Product");
        item.setQuantity(3);
        item.setProductPrice(new BigDecimal("49.99"));

        assertEquals(5L, item.getId());
        assertEquals(200L, item.getProductId());
        assertEquals("Updated Product", item.getProductName());
        assertEquals(3, item.getQuantity());
        assertEquals(new BigDecimal("49.99"), item.getProductPrice());
    }
}
