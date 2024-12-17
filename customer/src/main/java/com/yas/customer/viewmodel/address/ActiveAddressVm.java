package com.yas.customer.viewmodel.address;

import lombok.Builder;

@Builder(toBuilder = true)
public record ActiveAddressVm(
        Long id,
        String contactName,
        String phone,
        String addressLine1,
        String city,
        String zipCode,
        Long districtId,
        String districtName,
        Long stateOrProvinceId,
        String stateOrProvinceName,
        String stateOrProvinceCode,
        Long countryId,
        String countryName,
        String countryCode2,
        String countryCode3,
        Boolean isActive
) {

}
