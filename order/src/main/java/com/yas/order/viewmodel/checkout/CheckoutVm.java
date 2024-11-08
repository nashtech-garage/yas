package com.yas.order.viewmodel.checkout;

import com.yas.order.model.Checkout;
import com.yas.order.model.enumeration.CheckoutProgress;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.model.enumeration.PaymentMethod;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Builder;

@Builder(toBuilder = true)
public record CheckoutVm(
        String id,
        String email,
        String note,
        String couponCode,
        CheckoutState checkoutState,
        CheckoutProgress progress,
        PaymentMethod paymentMethodId,
        String attributes,
        String lastError,
        BigDecimal totalAmount,
        BigDecimal totalDiscountAmount,
        Set<CheckoutItemVm> checkoutItemVms
) {

    public static CheckoutVm fromModel(Checkout checkout, Set<CheckoutItemVm> checkoutItemVms) {
        return CheckoutVm.builder()
            .id(checkout.getId())
            .email(checkout.getEmail())
            .note(checkout.getNote())
            .couponCode(checkout.getCouponCode())
            .checkoutState(checkout.getCheckoutState())
            .progress(checkout.getProgress())
            .paymentMethodId(checkout.getPaymentMethodId())
            .attributes(checkout.getAttributes())
            .lastError(checkout.getLastError())
            .checkoutItemVms(checkoutItemVms)
            .build();
    }
}