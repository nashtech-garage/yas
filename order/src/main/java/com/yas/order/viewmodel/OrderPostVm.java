package com.yas.order.viewmodel;

import com.yas.order.model.enumeration.EDeliveryMethod;
import com.yas.order.model.enumeration.EDeliveryStatus;
import com.yas.order.model.enumeration.EPaymentMethod;
import com.yas.order.model.enumeration.EPaymentStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record OrderPostVm(
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
        EDeliveryMethod deliveryMethod,
        EDeliveryStatus deliveryStatus,
        EPaymentMethod paymentMethod,
        EPaymentStatus paymentStatus,
        @NotNull
        List<OrderItemPostVm> orderItemPostVms
) {
}