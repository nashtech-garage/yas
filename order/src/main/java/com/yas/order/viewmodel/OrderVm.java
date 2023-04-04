package com.yas.order.viewmodel;

import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record OrderVm(
        Long id,
        String phone,
        String email,
        Long shippingAddressId,
        Long billingAddressId,
        String note,
        float tax,
        float discount,
        int numberItem,
        BigDecimal totalPrice,
        BigDecimal deliveryFee,
        String couponCode,
        EOrderStatus orderStatus,
        EDeliveryMethod deliveryMethod,
        EDeliveryStatus deliveryStatus,
        EPaymentMethod paymentMethod,
        EPaymentStatus paymentStatus,
        Set<OrderItemVm> orderItemVms

) {
    public static OrderVm fromModel(Order order) {
        Set<OrderItemVm> orderItemVms = order.getOrderItems().stream().map(
                item -> OrderItemVm.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .productPrice(item.getProductPrice())
                        .note(item.getNote())
                        .discountAmount(item.getDiscountAmount())
                        .taxPercent(item.getTaxPercent())
                        .taxAmount(item.getTaxAmount())
                        .orderId(item.getOrderId().getId())
                        .build())
                .collect(Collectors.toSet());


        return OrderVm.builder()
                .id(order.getId())
                .phone(order.getPhone())
                .email(order.getEmail())
                .shippingAddressId(order.getShippingAddressId())
                .billingAddressId(order.getBillingAddressId())
                .note(order.getNote())
                .tax(order.getTax())
                .discount(order.getDiscount())
                .numberItem(order.getNumberItem())
                .totalPrice(order.getTotalPrice())
                .couponCode(order.getCouponCode())
                .orderStatus(order.getOrderStatus())
                .deliveryFee(order.getDeliveryFee())
                .deliveryMethod(order.getDeliveryMethod())
                .deliveryStatus(order.getDeliveryStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .orderItemVms(orderItemVms)
                .build();
    }
}