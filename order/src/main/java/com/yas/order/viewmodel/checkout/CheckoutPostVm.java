package com.yas.order.viewmodel.checkout;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record CheckoutPostVm(
        String email,
        String note,
        String promotionCode,
        String shipmentMethodId,
        String shipmentServiceId,
        String paymentMethodId,
        String shippingAddressId,
        @NotEmpty(message = "Checkout Items must not be empty")
        List<CheckoutItemPostVm> checkoutItemPostVms) {
}
