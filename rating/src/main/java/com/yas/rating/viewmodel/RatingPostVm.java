package com.yas.rating.viewmodel;

import com.yas.rating.model.Rating;

public record RatingPostVm(
        String content, int star, Long productId
) {

}

