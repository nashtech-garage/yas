package com.yas.order.viewmodel.checkout;

import com.yas.order.model.enumeration.CheckoutState;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Builder;

@Builder(toBuilder = true)
public record CheckoutVm(
        String id,
        String email,
        String note,
        String promotionCode,
        CheckoutState checkoutState,
        String progress,
        long totalAmount,
        BigDecimal totalShipmentFee,
        BigDecimal totalShipmentTax, 
        BigDecimal totalTax,
        BigDecimal totalDiscountAmount,
        String shipmentMethodId,
        String paymentMethodId,
        Long shippingAddressId,
        Set<CheckoutItemVm> checkoutItemVms) {

}
