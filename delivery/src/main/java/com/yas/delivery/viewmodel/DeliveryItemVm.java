package com.yas.delivery.viewmodel;

import jakarta.validation.constraints.NotNull;

public record DeliveryItemVm(@NotNull String productId,
                             @NotNull Integer quantity,
                             @NotNull Double weight,
                             Double length,
                             Double width,
                             Double height) {
}
