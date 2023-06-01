package com.yas.order.viewmodel.order;

import com.yas.order.model.Order;
import com.yas.order.model.enumeration.*;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record OrderBriefVm(
        Long id,
        String email,
        OrderAddressVm billingAddressVm,
        BigDecimal totalPrice,
        EOrderStatus orderStatus,
        EDeliveryMethod deliveryMethod,
        EDeliveryStatus deliveryStatus,
        EPaymentStatus paymentStatus,
        ZonedDateTime createdOn
        ) {
    public static OrderBriefVm fromModel(Order order) {
        return OrderBriefVm.builder()
                .id(order.getId())
                .email(order.getEmail())
                .billingAddressVm(OrderAddressVm.fromModel(order.getBillingAddressId()))
                .totalPrice(order.getTotalPrice())
                .orderStatus(order.getOrderStatus())
                .deliveryMethod(order.getDeliveryMethod())
                .deliveryStatus(order.getDeliveryStatus())
                .paymentStatus(order.getPaymentStatus())
                .createdOn(order.getCreatedOn())
                .build();
    }
}