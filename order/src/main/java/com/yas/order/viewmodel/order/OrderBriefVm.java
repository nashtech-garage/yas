package com.yas.order.viewmodel.order;

import com.yas.order.model.Order;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record OrderBriefVm(
        Long id,
        String email,
        OrderAddressVm billingAddressVm,
        BigDecimal totalPrice,
        OrderStatus orderStatus,
        DeliveryMethod deliveryMethod,
        DeliveryStatus deliveryStatus,
        PaymentStatus paymentStatus,
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