package com.yas.order.viewmodel.orderaddress;
import lombok.Builder;

@Builder
public record OrderAddressPostVm(
        String contactName,
        String phone,
        String addressLine1,
        String addressLine2,
        String city,
        String zipCode,
        Long districtId,
        Long stateOrProvinceId,
        Long countryId) {
}
