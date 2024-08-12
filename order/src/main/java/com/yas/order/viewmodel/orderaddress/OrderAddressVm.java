package com.yas.order.viewmodel.orderaddress;

import com.yas.order.model.OrderAddress;
import lombok.Builder;


@Builder
public record OrderAddressVm(
        Long id,
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
        Long countryId,
        String countryName) {
    public static OrderAddressVm fromModel(OrderAddress orderAddress) {
        return OrderAddressVm.builder()
                .id(orderAddress.getId())
                .phone(orderAddress.getPhone())
                .contactName(orderAddress.getContactName())
                .addressLine1(orderAddress.getAddressLine1())
                .addressLine2(orderAddress.getAddressLine2())
                .city(orderAddress.getCity())
                .zipCode(orderAddress.getZipCode())
                .districtId(orderAddress.getDistrictId())
                .districtName(orderAddress.getDistrictName())
                .stateOrProvinceId(orderAddress.getStateOrProvinceId())
                .stateOrProvinceName(orderAddress.getStateOrProvinceName())
                .countryId(orderAddress.getCountryId())
                .countryName(orderAddress.getCountryName())
                .build();
    }
}
