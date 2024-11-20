package com.yas.delivery.viewmodel;

import jakarta.validation.constraints.NotNull;

public record DeliveryAddress(@NotNull Long id,
                              @NotNull String addressLine1,
                              @NotNull String city,
                              @NotNull String zipCode,
                              @NotNull Long districtId,
                              @NotNull Long stateOrProvinceId,
                              @NotNull Long countryId) {
}
