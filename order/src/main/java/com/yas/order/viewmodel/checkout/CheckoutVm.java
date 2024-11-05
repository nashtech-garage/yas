package com.yas.order.viewmodel.checkout;

import com.yas.order.model.Checkout;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Builder;

@Builder(toBuilder = true)
public record CheckoutVm(
        String id,
        String email,
        String note,
        String couponCode,
        BigDecimal totalAmount,
        BigDecimal totalDiscountAmount,
        Set<CheckoutItemVm> checkoutItemVms
) {

    public static CheckoutVm fromModel(Checkout checkout, Set<CheckoutItemVm> checkoutItemVms) {
        return CheckoutVm.builder()
            .id(checkout.getId())
            .email(checkout.getEmail())
            .note(checkout.getNote())
            .couponCode(checkout.getCouponCode())
            .checkoutItemVms(checkoutItemVms)
            .build();
    }
}