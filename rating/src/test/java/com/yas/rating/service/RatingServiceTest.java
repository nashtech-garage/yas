package com.yas.rating.service;

import com.yas.rating.exception.NotFoundException;
import com.yas.rating.model.Rating;
import com.yas.rating.repository.RatingRepository;
import com.yas.rating.viewmodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class RatingServiceTest {
    RatingRepository ratingRepository;
    ProductService productService;
    CustomerService customerService;
    RatingService ratingService;

    RatingPostVm ratingPostVm;
    List<Rating> ratingList;

    @BeforeEach
    void setUp() {
        ratingRepository = mock(RatingRepository.class);
        productService = mock(ProductService.class);
        customerService = mock(CustomerService.class);
        ratingService = new RatingService(ratingRepository, productService, customerService);


        ratingList = List.of(
                Rating.builder()
                        .id(1L)
                        .content("comment 1")
                        .ratingStar(2)
                        .productId(1L)
                        .build(),
                Rating.builder()
                        .id(1L)
                        .content("comment 2")
                        .ratingStar(2)
                        .productId(1L)
                        .build()
        );

        ratingPostVm = new RatingPostVm(
                "comment 1",
                5,
                1L
        );

        //Security config
    }

    @Test
    void getRatingList_WhenProductIsExist_Sucsess() {
        int totalPage = 1;
        int pageNo = 0;
        int pageSize = 10;
        var pageCaptor = ArgumentCaptor.forClass(Pageable.class);
        Page<Rating> ratingPage = mock(Page.class);

        when(ratingRepository.findByProductId(anyLong(), any())).thenReturn(ratingPage);
        when(ratingPage.getContent()).thenReturn(ratingList);
        when(ratingPage.getTotalPages()).thenReturn(totalPage);
        when(ratingPage.getTotalElements()).thenReturn(2L);
        when(productService.getProductById(anyLong())).thenReturn(mock(ProductThumbnailVm.class));

        RatingListVm actualResponse = ratingService.getRatingListByProductId(1L, pageNo, pageSize);

        verify(ratingRepository).findByProductId(anyLong(), pageCaptor.capture());
        assertThat(actualResponse.totalPages()).isEqualTo(totalPage);
        assertThat(actualResponse.totalElements()).isEqualTo(2L);
        for (int i = 0; i < actualResponse.totalElements(); i++) {
            Rating rating = ratingList.get(i);
            assertThat(actualResponse.ratingList().get(i).id()).isEqualTo(rating.getId());
            assertThat(actualResponse.ratingList().get(i).star()).isEqualTo(rating.getRatingStar());
            assertThat(actualResponse.ratingList().get(i).content()).isEqualTo(rating.getContent());
            assertThat(actualResponse.ratingList().get(i).productId()).isEqualTo(rating.getProductId());
        }
    }

    @Test
    void getRatingList_WhenProductIsNotExist_ShouldThrowException() {
        Long productId = 1L;
        int pageNo = 0;
        int pageSize = 10;

        NotFoundException exception = assertThrows(NotFoundException.class, () -> ratingService.getRatingListByProductId(productId, pageNo, pageSize));

        assertThat(exception.getMessage()).isEqualTo(String.format("Product %s is not found", productId));
    }

    @Test
    void createProduct_WhenProductIsNotExist_ShouldThrowException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> ratingService.createRating(ratingPostVm));

        assertThat(exception.getMessage()).isEqualTo(String.format("Product %s is not found", ratingPostVm.productId()));
    }

    @Test
    void createProduct_WhenProductIsExist_ShouldReturnSuccess() {
        Rating savedRating = mock(Rating.class);
        var ratingCaptor = ArgumentCaptor.forClass(Rating.class);
        when(productService.getProductById(anyLong())).thenReturn(mock(ProductThumbnailVm.class));
        when(customerService.getCustomer()).thenReturn(mock(CustomerVm.class));
        when(ratingRepository.saveAndFlush(ratingCaptor.capture())).thenReturn(savedRating);


        RatingVm actualResponse = ratingService.createRating(ratingPostVm);

        verify(ratingRepository).saveAndFlush(ratingCaptor.capture());
        Rating ratingValue = ratingCaptor.getValue();
        assertThat(ratingValue.getContent()).isEqualTo(ratingPostVm.content());
        assertThat(ratingValue.getRatingStar()).isEqualTo(ratingPostVm.star());
        assertThat(ratingValue.getProductId()).isEqualTo(ratingPostVm.productId());
    }
}
