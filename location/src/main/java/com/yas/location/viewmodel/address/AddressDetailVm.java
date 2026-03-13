package com.yas.location.viewmodel.address;

import com.yas.location.model.Address;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddressDetailVm(Long id,
                              String contactName,
                              String phone,
                              String addressLine1,
                              String addressLine2,
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
                              String countryCode3) {
    public static AddressDetailVm fromModel(Address address) {
        return AddressDetailVm.builder()
            .id(address.getId())
            .contactName(address.getContactName())
            .phone(address.getPhone())
            .addressLine1(address.getAddressLine1())
            .addressLine2(address.getAddressLine2())
            .city(address.getCity())
            .districtId(address.getDistrict().getId())
            .districtName(address.getDistrict().getName())
            .stateOrProvinceId(address.getStateOrProvince().getId())
            .stateOrProvinceName(address.getStateOrProvince().getName())
            .stateOrProvinceCode(address.getStateOrProvince().getCode())
            .countryId(address.getCountry().getId())
            .countryName(address.getCountry().getName())
            .countryCode2(address.getCountry().getCode2())
            .countryCode3(address.getCountry().getCode3())
            .zipCode(address.getZipCode())
            .build();
    }
}