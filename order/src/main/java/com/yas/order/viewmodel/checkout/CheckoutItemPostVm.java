package com.yas.order.viewmodel.checkout;

import java.math.BigDecimal;

public record CheckoutItemPostVm(
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
