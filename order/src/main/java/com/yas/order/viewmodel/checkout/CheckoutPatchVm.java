package com.yas.order.viewmodel.checkout;

public record CheckoutPatchVm (
    String shipmentMethodId,
    String paymentMethodId,
    String shippingAddressId,
    String promotionCode
) {
}
