package com.yas.rating.service;

import com.yas.rating.model.Rating;
import com.yas.rating.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RatingServiceTest {
    RatingRepository ratingRepository;
    ProductService productService;
    RatingService ratingService;

    Rating rating;

    Authentication authentication;

    @BeforeEach
    void setUp() {
        ratingRepository = mock(RatingRepository.class);

        productService = mock(ProductService.class);
        ratingService = new RatingService(
                ratingRepository,
                productService);


        //Security config
        authentication = mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("Name");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getRatingList_WhenProductIsExist_Sucsess() {


    }
}
