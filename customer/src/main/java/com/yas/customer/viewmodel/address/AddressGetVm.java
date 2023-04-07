package com.yas.customer.viewmodel.Address;

import lombok.Builder;

@Builder
public record AddressGetVm(Long id, String contactName, String phone, String addressLine1, String city, String zipCode,
                           Long districtId, Long stateOrProvinceId, Long countryId) {

}
