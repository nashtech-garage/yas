package com.yas.order.viewmodel.promotion;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Builder
public record PromotionUsageVm(
        String promotionCode,
        Long productId,
        String userId,
        Long orderId
) {
}
