package com.yas.order.viewmodel.checkout;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Builder;

@Builder(toBuilder = true)
public record CheckoutVm(
        String id,
        String email,
        String note,
        String couponCode,
        BigDecimal totalAmount,
        BigDecimal totalDiscountAmount,
        Set<CheckoutItemVm> checkoutItemVms
) {
}