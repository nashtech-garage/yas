package com.yas.order.viewmodel.checkout;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CheckoutItemVm(
        Long id,
        Long productId,
        String productName,
        int quantity,
        BigDecimal productPrice,
        String note,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal taxPercent,
        String checkoutId) {
}
