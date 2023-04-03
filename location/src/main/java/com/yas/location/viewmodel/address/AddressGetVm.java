package com.yas.location.viewmodel.address;

import com.yas.location.model.Address;

public record AddressGetVm(String contactName, String phone, String addressLine1, String city, String zipCode,
                           Long districtId, Long stateOrProvinceId, Long countryId) {
    public static AddressGetVm fromModel(Address address) {
        return new AddressGetVm(address.getContactName(), address.getPhone(), address.getAddressLine1(),
                address.getCity(), address.getZipCode(), address.getDistrict().getId(),
                address.getStateOrProvince().getId(), address.getCountry().getId());
    }
}
