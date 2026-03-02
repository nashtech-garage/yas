package com.yas.order.viewmodel.checkout;

import jakarta.validation.constraints.Positive;

public record CheckoutItemPostVm(
        Long productId,
        String description,
        @Positive
        int quantity) {
}
