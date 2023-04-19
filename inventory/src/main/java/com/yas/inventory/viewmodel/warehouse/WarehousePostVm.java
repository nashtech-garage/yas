package com.yas.inventory.viewmodel.warehouse;

import jakarta.validation.constraints.NotBlank;

public record WarehousePostVm(String id, @NotBlank String name, String contactName, String phone, String addressLine1, String addressLine2, String city, String zipCode,
                              Long districtId, Long stateOrProvinceId, Long countryId) {

}