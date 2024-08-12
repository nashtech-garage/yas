package com.yas.order.viewmodel.checkout;

import com.yas.order.model.Checkout;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record CheckoutVm(
        String id,
        String email,
        String note,
        String couponCode,
        Set<CheckoutItemVm> checkoutItemVms

) {
    public static CheckoutVm fromModel(Checkout checkout) {
        Set<CheckoutItemVm> checkoutItemVms = checkout.getCheckoutItem().stream().map(
                        item -> CheckoutItemVm.fromModel(item))
                .collect(Collectors.toSet());
        return CheckoutVm.builder()
                .id(checkout.getId())
                .email(checkout.getEmail())
                .note(checkout.getNote())
                .couponCode(checkout.getCouponCode())
                .checkoutItemVms(checkoutItemVms)
                .build();
    }
}