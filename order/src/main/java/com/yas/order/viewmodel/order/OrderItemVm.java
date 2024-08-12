package com.yas.order.viewmodel.order;

import com.yas.order.model.OrderItem;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record OrderItemVm(
        Long id,
        Long productId,
        String productName,
        int quantity,
        BigDecimal productPrice,
        String note,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal taxPercent,
        Long orderId) {
    public static OrderItemVm fromModel(OrderItem orderItem) {
        return OrderItemVm.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .quantity(orderItem.getQuantity())
                .productPrice(orderItem.getProductPrice())
                .note(orderItem.getNote())
                .discountAmount(orderItem.getDiscountAmount())
                .taxPercent(orderItem.getTaxPercent())
                .taxAmount(orderItem.getTaxAmount())
                .orderId(orderItem.getOrderId().getId())
                .build();
    }
}
