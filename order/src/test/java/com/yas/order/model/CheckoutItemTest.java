package com.yas.order.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutItemTest {

    @Test
    void builderShouldCreateCheckoutItem() {
        CheckoutItem checkoutItem = CheckoutItem.builder()
            .id(1L)
            .productId(100L)
            .productName("Test Product")
            .description("Test Description")
            .quantity(2)
            .productPrice(BigDecimal.valueOf(10.50))
            .taxAmount(BigDecimal.valueOf(1.05))
            .shipmentFee(BigDecimal.valueOf(3.00))
            .shipmentTax(BigDecimal.valueOf(0.30))
            .discountAmount(BigDecimal.valueOf(2.00))
            .build();

        assertEquals(1L, checkoutItem.getId());
        assertEquals(100L, checkoutItem.getProductId());
        assertEquals("Test Product", checkoutItem.getProductName());
        assertEquals("Test Description", checkoutItem.getDescription());
        assertEquals(2, checkoutItem.getQuantity());
        assertEquals(BigDecimal.valueOf(10.50), checkoutItem.getProductPrice());
        assertEquals(BigDecimal.valueOf(1.05), checkoutItem.getTaxAmount());
        assertEquals(BigDecimal.valueOf(3.00), checkoutItem.getShipmentFee());
        assertEquals(BigDecimal.valueOf(0.30), checkoutItem.getShipmentTax());
        assertEquals(BigDecimal.valueOf(2.00), checkoutItem.getDiscountAmount());
    }

    @Test
    void equalsShouldReturnTrueForSameObject() {
        CheckoutItem checkoutItem = CheckoutItem.builder()
            .id(1L)
            .build();

        assertEquals(checkoutItem, checkoutItem);
    }

    @Test
    void equalsShouldReturnTrueForSameId() {
        CheckoutItem first = CheckoutItem.builder()
            .id(1L)
            .build();

        CheckoutItem second = CheckoutItem.builder()
            .id(1L)
            .build();

        assertEquals(first, second);
    }

    @Test
    void equalsShouldReturnFalseForDifferentId() {
        CheckoutItem first = CheckoutItem.builder()
            .id(1L)
            .build();

        CheckoutItem second = CheckoutItem.builder()
            .id(2L)
            .build();

        assertNotEquals(first, second);
    }

    @Test
    void equalsShouldReturnFalseForNullId() {
        CheckoutItem first = CheckoutItem.builder()
            .id(null)
            .build();

        CheckoutItem second = CheckoutItem.builder()
            .id(1L)
            .build();

        assertNotEquals(first, second);
    }

    @Test
    void equalsShouldReturnFalseForDifferentClass() {
        CheckoutItem checkoutItem = CheckoutItem.builder()
            .id(1L)
            .build();

        assertNotEquals("not checkout item", checkoutItem);
    }

    @Test
    void hashCodeShouldUseClassHashCode() {
        CheckoutItem checkoutItem = CheckoutItem.builder()
            .id(1L)
            .build();

        assertEquals(CheckoutItem.class.hashCode(), checkoutItem.hashCode());
    }
}
