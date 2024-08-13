package com.yas.location.viewmodel.address;

import com.yas.location.model.Address;
import lombok.Builder;

@Builder
public record AddressGetVm(Long id, String contactName, String phone, String addressLine1, String addressLine2,
                           String city, String zipCode,
                           Long districtId, Long stateOrProvinceId, Long countryId) {
    public static AddressGetVm fromModel(Address address) {
        return AddressGetVm.builder()
            .id(address.getId())
            .contactName(address.getContactName())
            .phone(address.getPhone())
            .addressLine1(address.getAddressLine1())
            .addressLine2(address.getAddressLine2())
            .city(address.getCity())
            .districtId(address.getDistrict().getId())
            .stateOrProvinceId(address.getStateOrProvince().getId())
            .countryId(address.getCountry().getId())
            .zipCode(address.getZipCode())
            .build();
    }
}
