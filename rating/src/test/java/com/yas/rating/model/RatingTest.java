package com.yas.rating.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RatingTest {

    @Test
    void testEqualsAndHashCode() {
        Rating rating1 = new Rating();
        rating1.setId(1L);

        Rating rating2 = new Rating();
        rating2.setId(1L);

        Rating rating3 = new Rating();
        rating3.setId(2L);

        assertEquals(rating1, rating2);
        assertNotEquals(rating1, rating3);
        assertNotEquals(rating1, new Object());
        assertTrue(rating1.equals(rating1));

        assertEquals(rating1.hashCode(), rating2.hashCode());
    }

    @Test
    void testGettersAndSetters() {
        Rating rating = Rating.builder()
                .id(1L)
                .content("Good")
                .ratingStar(5)
                .productId(10L)
                .productName("Phone")
                .lastName("Nguyen")
                .firstName("An")
                .build();

        assertEquals(1L, rating.getId());
        assertEquals("Good", rating.getContent());
        assertEquals(5, rating.getRatingStar());
        assertEquals(10L, rating.getProductId());
        assertEquals("Phone", rating.getProductName());
        assertEquals("Nguyen", rating.getLastName());
        assertEquals("An", rating.getFirstName());
    }
}
