package com.yas.order.viewmodel;

import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import lombok.Builder;

import java.util.Set;
import java.util.stream.Collectors;


@Builder
public record OrderAddressVm(
        Long id,
        String contactName,
        String phone,
        String addressLine1,
        String addressLine2,
        String city,
        String zipCode,
        Long district,
        Long stateOrProvince,
        Long country) {
    public static OrderAddressVm fromModel(OrderAddress orderAddress) {
        return OrderAddressVm.builder()
                .id(orderAddress.getId())
                .phone(orderAddress.getPhone())
                .contactName(orderAddress.getContactName())
                .addressLine1(orderAddress.getAddressLine1())
                .addressLine2(orderAddress.getAddressLine2())
                .city(orderAddress.getCity())
                .zipCode(orderAddress.getZipCode())
                .district(orderAddress.getDistrict())
                .stateOrProvince(orderAddress.getStateOrProvince())
                .country(orderAddress.getCountry())
                .build();
    }
}
