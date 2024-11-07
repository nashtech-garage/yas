package com.yas.order.viewmodel.checkout;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CheckoutItemVm(
        Long id,
        Long productId,
        String productName,
        String description,
        int quantity,
        BigDecimal productPrice,
        BigDecimal taxAmount,
        BigDecimal discountAmount,
        BigDecimal shipmentFee,
        BigDecimal shipmentTax,
        String checkoutId) {

}
