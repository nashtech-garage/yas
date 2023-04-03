package com.yas.rating.viewmodel;

import lombok.Builder;

@Builder
public record RatingPostVm(
        String content, int star, Long productId, String productName
) {

}

