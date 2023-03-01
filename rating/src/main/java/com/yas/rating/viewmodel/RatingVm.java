package com.yas.rating.viewmodel;



import com.yas.rating.model.Rating;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.Date;

@Builder
public record RatingVm(Long id, String content, int star, Long productId, String createdBy,String lastName, String firstName, String email, ZonedDateTime createdOn) {
    public static RatingVm fromModel(Rating rating) {
        return RatingVm.builder()
                .id(rating.getId())
                .content(rating.getContent())
                .star(rating.getRatingStar())
                .productId(rating.getProductId())
                .lastName(rating.getLastName())
                .firstName(rating.getFirstName())
                .email(rating.getEmail())
                .createdBy(rating.getCreatedBy())
                .createdOn(rating.getCreatedOn())
                .build();
    }
}
