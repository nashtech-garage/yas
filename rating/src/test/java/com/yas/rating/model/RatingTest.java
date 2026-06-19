package com.yas.rating.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RatingTest {

    private Rating rating1;
    private Rating rating2;

    @BeforeEach
    void setUp() {
        rating1 = new Rating();
        rating1.setId(1L);

        rating2 = new Rating();
        rating2.setId(1L);
    }

    // ==========================================
    // TESTS CHO HÀM EQUALS (PHỦ 100% CÁC NHÁNH IF/ELSE)
    // ==========================================

    @Test
    void testEquals_SameInstance_ShouldReturnTrue() {
        // Nhánh 1: (this == o)
        assertTrue(rating1.equals(rating1));
    }

    @Test
    void testEquals_NullOrDifferentClass_ShouldReturnFalse() {
        // Nhánh 2: (!(o instanceof Rating))
        assertFalse(rating1.equals(null));
        assertFalse(rating1.equals(new Object()));
    }

    @Test
    void testEquals_BothIdsNull_ShouldReturnFalse() {
        // Nhánh 3: id != null (Cả 2 đối tượng đều chưa có ID)
        Rating rating3 = new Rating();
        Rating rating4 = new Rating();
        assertFalse(rating3.equals(rating4));
    }

    @Test
    void testEquals_OneIdNull_ShouldReturnFalse() {
        // Nhánh 4: 1 đối tượng có ID, 1 đối tượng null ID
        Rating rating3 = new Rating();
        assertFalse(rating3.equals(rating1));
        assertFalse(rating1.equals(rating3));
    }

    @Test
    void testEquals_DifferentIds_ShouldReturnFalse() {
        // Nhánh 5: ID khác nhau
        rating2.setId(2L);
        assertFalse(rating1.equals(rating2));
    }

    @Test
    void testEquals_SameIds_ShouldReturnTrue() {
        // Nhánh 6: ID giống nhau hoàn toàn
        assertTrue(rating1.equals(rating2));
    }

    // ==========================================
    // TESTS CHO HÀM HASHCODE
    // ==========================================

    @Test
    void testHashCode_ShouldReturnClassHashCode() {
        assertEquals(rating1.getClass().hashCode(), rating1.hashCode());
    }

    // ==========================================
    // TESTS CHO LOMBOK GETTER/SETTER & BUILDER (TĂNG LINE COVERAGE)
    // ==========================================

    @Test
    void testGettersAndSetters() {
        Rating rating = new Rating();
        rating.setId(10L);
        rating.setContent("Sản phẩm rất tốt");
        rating.setRatingStar(5);
        rating.setProductId(100L);
        rating.setProductName("Laptop");
        rating.setLastName("Nguyễn");
        rating.setFirstName("Duy");

        assertEquals(10L, rating.getId());
        assertEquals("Sản phẩm rất tốt", rating.getContent());
        assertEquals(5, rating.getRatingStar());
        assertEquals(100L, rating.getProductId());
        assertEquals("Laptop", rating.getProductName());
        assertEquals("Nguyễn", rating.getLastName());
        assertEquals("Duy", rating.getFirstName());
    }

    @Test
    void testBuilder() {
        Rating rating = Rating.builder()
            .id(1L)
            .content("Giao hàng hơi chậm")
            .ratingStar(4)
            .productId(200L)
            .productName("Chuột không dây")
            .lastName("Quốc")
            .firstName("Duy")
            .build();
            
        assertNotNull(rating);
        assertEquals(1L, rating.getId());
        assertEquals("Giao hàng hơi chậm", rating.getContent());
        assertEquals(4, rating.getRatingStar());
        assertEquals(200L, rating.getProductId());
        assertEquals("Chuột không dây", rating.getProductName());
        assertEquals("Quốc", rating.getLastName());
        assertEquals("Duy", rating.getFirstName());
    }
}