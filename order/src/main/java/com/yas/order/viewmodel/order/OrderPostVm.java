package com.yas.order.viewmodel.order;

import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.PaymentMethod;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder
public record OrderPostVm(
        @NotBlank String checkoutId,
        @NotBlank String email,
        @NotNull OrderAddressPostVm shippingAddressPostVm,
        @NotNull OrderAddressPostVm billingAddressPostVm,
        String note,
        BigDecimal tax,
        BigDecimal discount,
        int numberItem,
        @NotNull BigDecimal totalPrice,
        BigDecimal deliveryFee,
        String couponCode,
        @NotNull DeliveryMethod deliveryMethod,
        @NotNull PaymentMethod paymentMethod,
        @NotNull PaymentStatus paymentStatus,
        @NotNull
        List<OrderItemPostVm> orderItemPostVms
) {
}