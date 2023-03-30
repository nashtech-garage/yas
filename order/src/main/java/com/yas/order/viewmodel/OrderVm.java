package com.yas.order.viewmodel;

import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.EDeliveryMethod;
import com.yas.order.model.enumeration.EDeliveryStatus;
import com.yas.order.model.enumeration.EPaymentMethod;
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
        String address,
        String note,
        float tax,
        float discount,
        int numberItem,
        BigDecimal totalPrice,
        BigDecimal deliveryFee,
        EDeliveryMethod deliveryMethod,
        EDeliveryStatus deliveryStatus,
        EPaymentMethod paymentMethod,
        List<OrderItemVm> orderItemVms

) {
    public static OrderVm fromModel(Order order) {
        List<OrderItemVm> orderItemVms = order.getOrderItems().stream().map(
                item -> OrderItemVm.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .note(item.getNote())
                        .orderId(item.getOrderId().getId())
                        .build())
                .collect(Collectors.toList());


        return OrderVm.builder()
                .id(order.getId())
                .phone(order.getPhone())
                .address(order.getAddress())
                .note(order.getNote())
                .tax(order.getTax())
                .discount(order.getDiscount())
                .numberItem(order.getNumberItem())
                .totalPrice(order.getTotalPrice())
                .deliveryFee(order.getDeliveryFee())
                .deliveryMethod(order.getDeliveryMethod())
                .deliveryStatus(order.getDeliveryStatus())
                .paymentMethod(order.getPaymentMethod())
                .orderItemVms(orderItemVms)
                .build();
    }
}