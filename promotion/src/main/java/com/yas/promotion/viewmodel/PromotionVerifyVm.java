package com.yas.promotion.viewmodel;

import java.util.List;

public record PromotionVerifyVm(
    String couponCode,
    Long orderPrice,
    List<Long> productIds
) {
}
