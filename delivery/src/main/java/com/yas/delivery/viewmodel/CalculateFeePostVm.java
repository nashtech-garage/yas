package com.yas.delivery.viewmodel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record CalculateFeePostVm(
    @NotNull(message = "Delivery provider is required") String deliveryProviderId,
    @Valid @NotNull(message = "Warehouse address is required") DeliveryAddressVm warehouseAddress,
    @Valid @NotNull(message = "Recipient address is required") DeliveryAddressVm recipientAddress,
    @NotEmpty(message = "Delivery items are required") List<@Valid DeliveryItemVm> deliveryItems) {
}