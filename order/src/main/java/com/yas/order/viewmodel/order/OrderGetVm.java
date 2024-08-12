package com.yas.order.viewmodel.order;

import com.yas.order.model.Order;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public record OrderGetVm(
        Long id,
        OrderStatus orderStatus,
        BigDecimal totalPrice,
        DeliveryStatus deliveryStatus,
        DeliveryMethod deliveryMethod,
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
