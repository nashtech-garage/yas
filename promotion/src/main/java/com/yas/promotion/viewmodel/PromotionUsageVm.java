package com.yas.promotion.viewmodel;

public record PromotionUsageVm(
        String promotionCode,
        Long productId,
        String userId,
        Long orderId
) {
}
