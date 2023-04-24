package com.yas.inventory.viewmodel.warehouse;

public record WarehouseDetailVm(Long id, String name, String contactName, String phone, String addressLine1, String addressLine2, String city, String zipCode,
                                Long districtId, Long stateOrProvinceId, Long countryId) {
}