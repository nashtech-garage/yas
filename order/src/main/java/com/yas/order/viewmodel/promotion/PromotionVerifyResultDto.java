package com.yas.order.viewmodel.promotion;

public record PromotionVerifyResultDto(
    boolean isValid,
    Long productId,
    String couponCode,
    DiscountType discountType,
    Long discountValue) {
}