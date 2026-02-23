package com.yas.rating.viewmodel;

import com.yas.rating.model.Rating;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RatingVmTest {

    @Test
    void testFromModel_shouldMapAllFields() {
        // Given
        Rating rating = Rating.builder()
                .id(1L)
                .content("Great product")
                .ratingStar(5)
                .productId(100L)
                .productName("Test Product")
                .lastName("Doe")
                .firstName("John")
                .build();
        rating.setCreatedBy("user-123");
        rating.setCreatedOn(ZonedDateTime.now());

        // When
        RatingVm ratingVm = RatingVm.fromModel(rating);

        // Then
        assertThat(ratingVm).isNotNull();
        assertThat(ratingVm.id()).isEqualTo(1L);
        assertThat(ratingVm.content()).isEqualTo("Great product");
        assertThat(ratingVm.star()).isEqualTo(5);
        assertThat(ratingVm.productId()).isEqualTo(100L);
        assertThat(ratingVm.productName()).isEqualTo("Test Product");
        assertThat(ratingVm.lastName()).isEqualTo("Doe");
        assertThat(ratingVm.firstName()).isEqualTo("John");
        assertThat(ratingVm.createdBy()).isEqualTo("user-123");
        assertThat(ratingVm.createdOn()).isNotNull();
    }

    @Test
    void testBuilder_shouldCreateRatingVm() {
        // Given
        ZonedDateTime now = ZonedDateTime.now();

        // When
        RatingVm ratingVm = RatingVm.builder()
                .id(2L)
                .content("Nice")
                .star(4)
                .productId(200L)
                .productName("Product 2")
                .lastName("Smith")
                .firstName("Jane")
                .createdBy("user-456")
                .createdOn(now)
                .build();

        // Then
        assertThat(ratingVm).isNotNull();
        assertThat(ratingVm.id()).isEqualTo(2L);
        assertThat(ratingVm.content()).isEqualTo("Nice");
        assertThat(ratingVm.star()).isEqualTo(4);
        assertThat(ratingVm.productId()).isEqualTo(200L);
        assertThat(ratingVm.productName()).isEqualTo("Product 2");
        assertThat(ratingVm.lastName()).isEqualTo("Smith");
        assertThat(ratingVm.firstName()).isEqualTo("Jane");
        assertThat(ratingVm.createdBy()).isEqualTo("user-456");
        assertThat(ratingVm.createdOn()).isEqualTo(now);
    }

    @Test
    void testFromModel_withNullValues_shouldHandleGracefully() {
        // Given
        Rating rating = Rating.builder()
                .id(3L)
                .build();

        // When
        RatingVm ratingVm = RatingVm.fromModel(rating);

        // Then
        assertThat(ratingVm).isNotNull();
        assertThat(ratingVm.id()).isEqualTo(3L);
        assertThat(ratingVm.content()).isNull();
        assertThat(ratingVm.star()).isZero();
        assertThat(ratingVm.productId()).isNull();
        assertThat(ratingVm.productName()).isNull();
        assertThat(ratingVm.lastName()).isNull();
        assertThat(ratingVm.firstName()).isNull();
    }
}
