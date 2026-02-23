package com.yas.rating.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RatingTest {

    @Test
    void testBuilder_shouldCreateRatingWithAllFields() {
        // When
        Rating rating = Rating.builder()
                .id(1L)
                .content("Great product!")
                .ratingStar(5)
                .productId(100L)
                .productName("Test Product")
                .lastName("Doe")
                .firstName("John")
                .build();

        // Then
        assertThat(rating).isNotNull();
        assertThat(rating.getId()).isEqualTo(1L);
        assertThat(rating.getContent()).isEqualTo("Great product!");
        assertThat(rating.getRatingStar()).isEqualTo(5);
        assertThat(rating.getProductId()).isEqualTo(100L);
        assertThat(rating.getProductName()).isEqualTo("Test Product");
        assertThat(rating.getLastName()).isEqualTo("Doe");
        assertThat(rating.getFirstName()).isEqualTo("John");
    }

    @Test
    void testNoArgsConstructor_shouldCreateEmptyRating() {
        // When
        Rating rating = new Rating();

        // Then
        assertThat(rating).isNotNull();
        assertThat(rating.getId()).isNull();
        assertThat(rating.getContent()).isNull();
        assertThat(rating.getRatingStar()).isZero();
    }

    @Test
    void testAllArgsConstructor_shouldCreateRating() {
        // When
        Rating rating = new Rating(1L, "Nice!", 4, 200L, "Product2", "Smith", "Jane");

        // Then
        assertThat(rating).isNotNull();
        assertThat(rating.getId()).isEqualTo(1L);
        assertThat(rating.getContent()).isEqualTo("Nice!");
        assertThat(rating.getRatingStar()).isEqualTo(4);
        assertThat(rating.getProductId()).isEqualTo(200L);
        assertThat(rating.getProductName()).isEqualTo("Product2");
        assertThat(rating.getLastName()).isEqualTo("Smith");
        assertThat(rating.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testSettersAndGetters_shouldWorkCorrectly() {
        // Given
        Rating rating = new Rating();

        // When
        rating.setId(3L);
        rating.setContent("Average");
        rating.setRatingStar(3);
        rating.setProductId(300L);
        rating.setProductName("Product3");
        rating.setLastName("Johnson");
        rating.setFirstName("Bob");

        // Then
        assertThat(rating.getId()).isEqualTo(3L);
        assertThat(rating.getContent()).isEqualTo("Average");
        assertThat(rating.getRatingStar()).isEqualTo(3);
        assertThat(rating.getProductId()).isEqualTo(300L);
        assertThat(rating.getProductName()).isEqualTo("Product3");
        assertThat(rating.getLastName()).isEqualTo("Johnson");
        assertThat(rating.getFirstName()).isEqualTo("Bob");
    }

    @Test
    void testEquals_whenSameId_shouldReturnTrue() {
        // Given
        Rating rating1 = Rating.builder().id(1L).content("Test").build();
        Rating rating2 = Rating.builder().id(1L).content("Different").build();

        // When & Then
        assertThat(rating1.equals(rating2)).isTrue();
    }

    @Test
    void testEquals_whenDifferentId_shouldReturnFalse() {
        // Given
        Rating rating1 = Rating.builder().id(1L).content("Test").build();
        Rating rating2 = Rating.builder().id(2L).content("Test").build();

        // When & Then
        assertThat(rating1.equals(rating2)).isFalse();
    }

    @Test
    void testEquals_whenSameObject_shouldReturnTrue() {
        // Given
        Rating rating = Rating.builder().id(1L).build();

        // When & Then
        assertThat(rating.equals(rating)).isTrue();
    }

    @Test
    void testEquals_whenNullId_shouldReturnFalse() {
        // Given
        Rating rating1 = Rating.builder().content("Test").build();
        Rating rating2 = Rating.builder().content("Test").build();

        // When & Then
        assertThat(rating1.equals(rating2)).isFalse();
    }

    @Test
    void testEquals_whenNotRatingInstance_shouldReturnFalse() {
        // Given
        Rating rating = Rating.builder().id(1L).build();
        String notRating = "Not a rating";

        // When & Then
        assertThat(rating.equals(notRating)).isFalse();
    }

    @Test
    void testHashCode_shouldBeConsistent() {
        // Given
        Rating rating = Rating.builder().id(1L).content("Test").build();

        // When
        int hashCode1 = rating.hashCode();
        int hashCode2 = rating.hashCode();

        // Then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }
}
