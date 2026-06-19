package com.yas.order.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CheckoutItemTest {

    private CheckoutItem item1;
    private CheckoutItem item2;

    @BeforeEach
    void setUp() {
        item1 = new CheckoutItem();
        item1.setId(1L);

        item2 = new CheckoutItem();
        item2.setId(1L);
    }

    // ==========================================
    // TESTS CHO HÀM EQUALS (PHỦ 100% CÁC NHÁNH IF/ELSE)
    // ==========================================

    @Test
    void testEquals_SameInstance_ShouldReturnTrue() {
        // Nhánh 1: (this == o)
        assertTrue(item1.equals(item1));
    }

    @Test
    void testEquals_NullOrDifferentClass_ShouldReturnFalse() {
        // Nhánh 2: (!(o instanceof CheckoutItem))
        assertFalse(item1.equals(null));
        assertFalse(item1.equals(new Object()));
    }

    @Test
    void testEquals_BothIdsNull_ShouldReturnFalse() {
        // Nhánh 3: id != null (cả 2 đều null)
        CheckoutItem item3 = new CheckoutItem();
        CheckoutItem item4 = new CheckoutItem();
        assertFalse(item3.equals(item4));
    }

    @Test
    void testEquals_OneIdNull_ShouldReturnFalse() {
        // Nhánh 4: id != null (1 cái null, 1 cái có ID)
        CheckoutItem item3 = new CheckoutItem();
        assertFalse(item3.equals(item1));
        assertFalse(item1.equals(item3));
    }

    @Test
    void testEquals_DifferentIds_ShouldReturnFalse() {
        // Nhánh 5: So sánh ID khác nhau
        item2.setId(2L);
        assertFalse(item1.equals(item2));
    }

    @Test
    void testEquals_SameIds_ShouldReturnTrue() {
        // Nhánh 6: So sánh ID giống nhau hoàn toàn
        assertTrue(item1.equals(item2));
    }

    // ==========================================
    // TESTS CHO HÀM HASHCODE
    // ==========================================

    @Test
    void testHashCode_ShouldReturnClassHashCode() {
        assertEquals(item1.getClass().hashCode(), item1.hashCode());
    }

    // ==========================================
    // TESTS CHO LOMBOK GETTER/SETTER & BUILDER (TĂNG ĐIỂM LINE COVERAGE)
    // ==========================================

    @Test
    void testGettersAndSetters() {
        CheckoutItem item = new CheckoutItem();
        item.setId(10L);
        item.setProductId(100L);
        item.setProductName("Test Product");
        item.setQuantity(2);
        item.setProductPrice(new BigDecimal("10.00"));
        
        assertEquals(10L, item.getId());
        assertEquals(100L, item.getProductId());
        assertEquals("Test Product", item.getProductName());
        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("10.00"), item.getProductPrice());
    }
    
    @Test
    void testBuilder() {
        CheckoutItem item = CheckoutItem.builder()
            .id(1L)
            .productName("Builder Product")
            .quantity(5)
            .build();
            
        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals("Builder Product", item.getProductName());
        assertEquals(5, item.getQuantity());
    }
}