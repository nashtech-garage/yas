package com.yas.rating.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RatingTest {

    @Test
    void equals_WhenSameReference_ShouldReturnTrue() {
        Rating rating = new Rating();

        assertTrue(rating.equals(rating));
    }

    @Test
    void equals_WhenComparedWithNull_ShouldReturnFalse() {
        Rating rating = new Rating();

        assertFalse(rating.equals(null));
    }

    @Test
    void equals_WhenComparedWithDifferentType_ShouldReturnFalse() {
        Rating rating = new Rating();

        assertFalse(rating.equals("not-a-rating"));
    }

    @Test
    void equals_WhenBothIdsNull_ShouldReturnFalse() {
        Rating first = new Rating();
        Rating second = new Rating();

        assertFalse(first.equals(second));
    }

    @Test
    void equals_WhenIdsEqual_ShouldReturnTrue() {
        Rating first = new Rating();
        first.setId(10L);
        Rating second = new Rating();
        second.setId(10L);

        assertTrue(first.equals(second));
    }

    @Test
    void equals_WhenIdsDifferent_ShouldReturnFalse() {
        Rating first = new Rating();
        first.setId(10L);
        Rating second = new Rating();
        second.setId(11L);

        assertFalse(first.equals(second));
    }

    @Test
    void hashCode_ShouldUseClassHashCode() {
        Rating first = new Rating();
        first.setId(10L);
        Rating second = new Rating();
        second.setId(99L);

        assertEquals(Rating.class.hashCode(), first.hashCode());
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(0, first.hashCode());
    }
}
