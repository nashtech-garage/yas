package com.yas.customer.viewmodel.address;

import lombok.Builder;

@Builder
public record AddressDetailVm(Long id, String contactName, String phone,
                              String addressLine1, String city, String zipCode,
                              Long districtId, String districtName, Long stateOrProvinceId,
                              String stateOrProvinceName, Long countryId, String countryName) {
}