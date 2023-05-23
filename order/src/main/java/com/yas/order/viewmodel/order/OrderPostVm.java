package com.yas.order.viewmodel.order;

import com.yas.order.model.enumeration.EDeliveryMethod;
import com.yas.order.model.enumeration.EPaymentMethod;
import com.yas.order.model.enumeration.EPaymentStatus;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record OrderPostVm(
        String checkoutId,
        String email,
        OrderAddressPostVm shippingAddressPostVm,
        OrderAddressPostVm billingAddressPostVm,
        String note,
        float tax,
        float discount,
        int numberItem,
        BigDecimal totalPrice,
        BigDecimal deliveryFee,
        String couponCode,
        EDeliveryMethod deliveryMethod,
        EPaymentMethod paymentMethod,
        EPaymentStatus paymentStatus,
        @NotNull
        List<OrderItemPostVm> orderItemPostVms
) {
}