package com.yas.delivery.viewmodel;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CheckoutItemVm(@NotNull String id,
                             @NotNull String productId,
                             @NotNull @Min(1) Integer quantity) {
}
