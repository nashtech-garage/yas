package com.yas.order.viewmodel;

import com.yas.order.model.Order;
import com.yas.order.model.enumeration.EDeliveryMethod;
import com.yas.order.model.enumeration.EDeliveryStatus;
import com.yas.order.model.enumeration.EOrderStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public record OrderGetVm(
        Long id,
        EOrderStatus orderStatus,
        BigDecimal totalPrice,
        EDeliveryStatus deliveryStatus,
        EDeliveryMethod deliveryMethod,
        List<OrderItemGetVm> orderItems,

        ZonedDateTime createdOn
) {
    public static OrderGetVm fromModel(Order order) {
        return new OrderGetVm(
                order.getId(),
                order.getOrderStatus(),
                order.getTotalPrice(),
                order.getDeliveryStatus(),
                order.getDeliveryMethod(),
                OrderItemGetVm.fromModels(order.getOrderItems()),
                order.getCreatedOn());
    }
}
