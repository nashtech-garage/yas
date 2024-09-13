package com.yas.order.viewmodel.checkout;

import java.util.Set;
import lombok.Builder;

@Builder(toBuilder = true)
public record CheckoutVm(
        String id,
        String email,
        String note,
        String couponCode,
        Set<CheckoutItemVm> checkoutItemVms
) {
}