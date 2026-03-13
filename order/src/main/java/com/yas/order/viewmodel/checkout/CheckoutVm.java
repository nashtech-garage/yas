package com.yas.order.viewmodel.checkout;

import com.yas.order.model.CheckoutAddress;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.viewmodel.customer.ActiveAddressVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
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
        BigDecimal totalAmount,
        BigDecimal totalShipmentFee,
        BigDecimal totalShipmentTax, 
        BigDecimal totalTax,
        BigDecimal totalDiscountAmount,
        String shipmentMethodId,
        String shipmentServiceId,
        String paymentMethodId,
        Long shippingAddressId,
        ActiveAddressVm shippingAddressDetail,
        Set<CheckoutItemVm> checkoutItemVms) {
}
