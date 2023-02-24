package com.yas.rating.viewmodel;



import com.yas.rating.model.Rating;

import java.util.Date;

public record RatingVm(Long id, String content, int star, Long productId, String createdBy) {
    public static RatingVm fromModel(Rating rating) {
        return new RatingVm(rating.getId(),
                rating.getContent(),
                rating.getRatingStar(),
                rating.getProductId(),
                rating.getCreatedBy()
        );
    }
}
