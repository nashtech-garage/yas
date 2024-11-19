package com.yas.order.viewmodel.checkout;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record CheckoutPostVm(
        String email,
        String note,
        String couponCode,
        BigDecimal totalAmount,
        BigDecimal totalDiscountAmount,
        @NotNull
        List<CheckoutItemPostVm> checkoutItemVms
) {
}
