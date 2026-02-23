package com.yas.rating.viewmodel;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RatingPostVmTest {

    @Test
    void testBuilder_shouldCreateRatingPostVm() {
        // When
        RatingPostVm ratingPostVm = RatingPostVm.builder()
                .content("Great product!")
                .star(5)
                .productId(100L)
                .productName("Test Product")
                .build();

        // Then
        assertThat(ratingPostVm).isNotNull();
        assertThat(ratingPostVm.content()).isEqualTo("Great product!");
        assertThat(ratingPostVm.star()).isEqualTo(5);
        assertThat(ratingPostVm.productId()).isEqualTo(100L);
        assertThat(ratingPostVm.productName()).isEqualTo("Test Product");
    }

    @Test
    void testRecord_shouldWorkWithAllFields() {
        // When
        RatingPostVm ratingPostVm = new RatingPostVm(
                "Nice product",
                4,
                200L,
                "Product 2");

        // Then
        assertThat(ratingPostVm.content()).isEqualTo("Nice product");
        assertThat(ratingPostVm.star()).isEqualTo(4);
        assertThat(ratingPostVm.productId()).isEqualTo(200L);
        assertThat(ratingPostVm.productName()).isEqualTo("Product 2");
    }

    @Test
    void testRecord_withNullContent_shouldAcceptNull() {
        // When
        RatingPostVm ratingPostVm = RatingPostVm.builder()
                .content(null)
                .star(3)
                .productId(300L)
                .productName("Product 3")
                .build();

        // Then
        assertThat(ratingPostVm.content()).isNull();
        assertThat(ratingPostVm.star()).isEqualTo(3);
    }
}
