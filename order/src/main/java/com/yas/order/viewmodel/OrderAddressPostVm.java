package com.yas.order.viewmodel;
import lombok.Builder;

@Builder
public record OrderAddressPostVm(
        String contactName,
        String phone,
        String addressLine1,
        String addressLine2,
        String city,
        String zipCode,
        Long district,
        Long stateOrProvince,
        Long country,
        Long billingOrderId,
        Long shippingOrderId) {
}
