package com.yas.order.viewmodel.checkout;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CheckoutPostVm(
        String email,
        String note,
        String couponCode,
        BigDecimal totalAmount,
        BigDecimal totalDiscountAmount,
        @NotNull
        List<CheckoutItemPostVm> checkoutItemPostVms
) {
}
