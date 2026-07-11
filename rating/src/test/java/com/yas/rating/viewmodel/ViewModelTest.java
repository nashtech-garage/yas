package com.yas.rating.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.rating.model.Rating;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class ViewModelTest {

    @Test
    void testRatingVm_fromModel() {
        Rating rating = Rating.builder()
            .id(1L)
            .content("content")
            .ratingStar(5)
            .productId(2L)
            .productName("product")
            .firstName("first")
            .lastName("last")
            .build();
        rating.setCreatedBy("user");
        rating.setCreatedOn(ZonedDateTime.now());

        RatingVm vm = RatingVm.fromModel(rating);
        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.content()).isEqualTo("content");
        assertThat(vm.star()).isEqualTo(5);
        assertThat(vm.productId()).isEqualTo(2L);
        assertThat(vm.productName()).isEqualTo("product");
        assertThat(vm.createdBy()).isEqualTo("user");
        assertThat(vm.firstName()).isEqualTo("first");
        assertThat(vm.lastName()).isEqualTo("last");
    }

    @Test
    void testRatingListVm() {
        RatingVm vm = new RatingVm(1L, "c", 5, 1L, "p", "u", "f", "l", null);
        RatingListVm listVm = new RatingListVm(List.of(vm), 1L, 1);
        assertThat(listVm.ratingList()).hasSize(1);
        assertThat(listVm.totalElements()).isEqualTo(1L);
        assertThat(listVm.totalPages()).isEqualTo(1);
    }

    @Test
    void testErrorVm() {
        ErrorVm errorVm = new ErrorVm("404", "Not Found", "Detail", List.of("field error"));
        assertThat(errorVm.statusCode()).isEqualTo("404");
        assertThat(errorVm.title()).isEqualTo("Not Found");
        assertThat(errorVm.detail()).isEqualTo("Detail");
        assertThat(errorVm.fieldErrors()).hasSize(1);
    }
}
