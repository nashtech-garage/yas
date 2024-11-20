package com.yas.delivery.viewmodel;

import jakarta.validation.constraints.NotNull;

public record DeliveryAddressVm(@NotNull(message = "Delivery address ID is required") Long id,
                                @NotNull(message = "Address line 1 is required") String addressLine1,
                                String city,
                                @NotNull(message = "Zipcode is required") String zipCode,
                                Long districtId,
                                Long stateOrProvinceId,
                                @NotNull(message = "Country ID is required") Long countryId) {
}