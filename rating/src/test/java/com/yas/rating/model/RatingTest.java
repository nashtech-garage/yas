package com.yas.rating.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class RatingTest {

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
