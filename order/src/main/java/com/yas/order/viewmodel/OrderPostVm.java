package com.yas.order.viewmodel;

import com.yas.order.model.enumeration.EDeliveryMethod;
import com.yas.order.model.enumeration.EDeliveryStatus;
import com.yas.order.model.enumeration.EPaymentMethod;

import java.math.BigDecimal;
import java.util.Set;

public record OrderPostVm(
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
        Set<OrderItemPostVm> orderItemPostVms
) {
}