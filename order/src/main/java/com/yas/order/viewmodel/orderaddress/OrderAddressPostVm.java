package com.yas.order.viewmodel.orderaddress;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderAddressPostVm(
        @NotBlank String contactName,
        @NotBlank String phone,
        @NotBlank String addressLine1,
        String addressLine2,
        @NotBlank String city,
        @NotBlank String zipCode,
        @NotNull Long districtId,
        @NotBlank String districtName,
        @NotNull Long stateOrProvinceId,
        @NotBlank String stateOrProvinceName,
        @NotNull Long countryId,
        @NotBlank String countryName) {
}
