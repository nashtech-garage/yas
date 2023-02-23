package com.yas.rating.viewmodel;



import com.yas.rating.model.Rating;

import java.util.Date;

public record RatingVm(Long id, String content, int star, String customerId, Long productId) {
    public static RatingVm fromModel(Rating rating) {
        return new RatingVm(rating.getId(),
                rating.getContent(),
                rating.getRatingStar(),
                rating.getCustomerId(),
                rating.getProductId()
        );
    }
}
