package com.yas.location.viewmodel.address;

import com.yas.location.model.Address;

public record AddressPostVm(String contactName, String phone, String addressLine1, String city, String zipCode,
                            Long districtId, Long stateOrProvinceId, Long countryId) {

    public static Address fromModel(AddressPostVm dto) {
        return Address.builder()
                .contactName(dto.contactName)
                .phone(dto.phone)
                .addressLine1(dto.addressLine1)
                .city(dto.city())
                .zipCode(dto.zipCode)
                .build();
    }
}
