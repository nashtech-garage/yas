package com.yas.order.viewmodel.checkout;

import com.yas.order.model.CheckoutItem;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CheckoutItemVm(
        Long id,
        Long productId,
        String productName,
        int quantity,
        BigDecimal productPrice,
        String note,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal taxPercent,
        String checkoutId) {
    public static CheckoutItemVm fromModel(CheckoutItem checkoutItem) {
        return CheckoutItemVm.builder()
                .id(checkoutItem.getId())
                .productId(checkoutItem.getProductId())
                .productName(checkoutItem.getProductName())
                .quantity(checkoutItem.getQuantity())
                .productPrice(checkoutItem.getProductPrice())
                .note(checkoutItem.getNote())
                .discountAmount(checkoutItem.getDiscountAmount())
                .taxPercent(checkoutItem.getTaxPercent())
                .taxAmount(checkoutItem.getTaxAmount())
                .checkoutId(checkoutItem.getCheckoutId().getId())
                .build();
    }
}
