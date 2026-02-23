package com.yas.rating.viewmodel;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RatingListVmTest {

    @Test
    void testConstructor_shouldCreateRatingListVm() {
        // Given
        RatingVm rating1 = RatingVm.builder()
                .id(1L)
                .content("Great")
                .star(5)
                .build();
        RatingVm rating2 = RatingVm.builder()
                .id(2L)
                .content("Good")
                .star(4)
                .build();
        List<RatingVm> ratingList = Arrays.asList(rating1, rating2);

        // When
        RatingListVm ratingListVm = new RatingListVm(ratingList, 2L, 1);

        // Then
        assertThat(ratingListVm).isNotNull();
        assertThat(ratingListVm.ratingList()).hasSize(2);
        assertThat(ratingListVm.totalElements()).isEqualTo(2L);
        assertThat(ratingListVm.totalPages()).isEqualTo(1);
    }

    @Test
    void testConstructor_withEmptyList_shouldWork() {
        // When
        RatingListVm ratingListVm = new RatingListVm(List.of(), 0L, 0);

        // Then
        assertThat(ratingListVm.ratingList()).isEmpty();
        assertThat(ratingListVm.totalElements()).isZero();
        assertThat(ratingListVm.totalPages()).isZero();
    }

    @Test
    void testConstructor_withMultiplePages_shouldWork() {
        // Given
        RatingVm rating = RatingVm.builder().id(1L).build();
        List<RatingVm> ratingList = List.of(rating);

        // When
        RatingListVm ratingListVm = new RatingListVm(ratingList, 25L, 3);

        // Then
        assertThat(ratingListVm.ratingList()).hasSize(1);
        assertThat(ratingListVm.totalElements()).isEqualTo(25L);
        assertThat(ratingListVm.totalPages()).isEqualTo(3);
    }
}
