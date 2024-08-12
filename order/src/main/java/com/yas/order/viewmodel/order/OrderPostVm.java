package com.yas.order.viewmodel.order;

import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.PaymentMethod;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder
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
        DeliveryMethod deliveryMethod,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        @NotNull
        List<OrderItemPostVm> orderItemPostVms
) {
}