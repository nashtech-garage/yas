package com.yas.order.viewmodel.checkout;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record CheckoutPostVm(
        String email,
        String note,
        String promotionCode,
        String shipmentMethodId,
        String paymentMethodId,
        String shippingAddressId,
        @NotNull
        List<CheckoutItemPostVm> checkoutItemPostVms) {

}
