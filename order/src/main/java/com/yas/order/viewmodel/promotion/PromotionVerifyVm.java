package com.yas.order.viewmodel.promotion;

import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record PromotionVerifyVm(
    String couponCode,
    Long orderPrice,
    List<Long> productIds
) {
}
