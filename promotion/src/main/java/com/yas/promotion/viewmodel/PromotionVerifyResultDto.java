package com.yas.promotion.viewmodel;

import com.yas.promotion.model.enumeration.DiscountType;

public record PromotionVerifyResultDto(
    boolean isValid,
    Long productId,
    String couponCode,
    DiscountType discountType,
    Long discountValue) {
}
