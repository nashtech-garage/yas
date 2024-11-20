package com.yas.delivery.viewmodel;

import jakarta.validation.constraints.NotNull;

public record DeliveryItemVm(@NotNull(message = "Delivery item product ID is required") String productId,
                             @NotNull(message = "Delivery item quantity is required") Integer quantity,
                             @NotNull(message = "Delivery item weight is required") Double weight,
                             Double length,
                             Double width,
                             Double height) {}