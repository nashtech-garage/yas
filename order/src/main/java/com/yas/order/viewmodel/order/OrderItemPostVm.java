package com.yas.order.viewmodel.order;

import lombok.Builder;

import java.math.BigDecimal;

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
