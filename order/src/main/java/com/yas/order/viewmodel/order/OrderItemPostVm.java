package com.yas.order.viewmodel.order;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record OrderItemPostVm(
        Long productId,
        String productName,
        int quantity,
        BigDecimal productPrice,
        String note,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal taxPercent
) {
}
