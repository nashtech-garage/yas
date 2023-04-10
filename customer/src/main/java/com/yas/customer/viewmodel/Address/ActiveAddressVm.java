package com.yas.customer.viewmodel.Address;

public record ActiveAddressVm(
        Long id,
        String contactName,
        String phone,
        String addressLine1,
        String city,
        String zipCode,
        Long districtId,
        Long stateOrProvinceId,
        Long countryId,
        Boolean isActive
) {

}