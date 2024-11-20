package com.yas.delivery.viewmodel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CalculateFeesPostVm(
    @Valid @NotNull DeliveryAddress wareHouseAddress,
    @Valid @NotNull DeliveryAddress recipientAddress,
    @NotEmpty List<@Valid DeliveryItemVm> deliveryItems) {
}
