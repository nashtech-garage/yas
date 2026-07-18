package com.yas.rating.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class RatingTest {

    @Test
    void testEqualsAndHashCode() {
        Rating r1 = new Rating();
        r1.setId(1L);
        Rating r2 = new Rating();
        r2.setId(1L);
        Rating r3 = new Rating();
        r3.setId(2L);

        assertEquals(r1, r2);
        assertEquals(r1, r1);
        assertNotEquals(r1, r3);
        assertNotEquals(r1, new Object());
        assertNotEquals(r1, null);

        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void equals_whenIdsMatch_thenReturnsTrue() {
        Rating rating = Rating.builder().id(1L).content("Nice").build();
        Rating sameId = Rating.builder().id(1L).content("Different").build();

        assertEquals(rating, sameId);
        assertEquals(rating.hashCode(), sameId.hashCode());
    }

    @Test
    void equals_whenComparedWithDifferentTypeOrNullId_thenReturnsFalse() {
        Rating rating = Rating.builder().id(1L).build();
        Rating noId = Rating.builder().build();

        assertNotEquals(rating, "rating");
        assertFalse(noId.equals(Rating.builder().id(1L).build()));
    }
}
