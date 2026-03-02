package com.yas.order.viewmodel.checkout;

import com.yas.order.viewmodel.enumeration.DimensionUnit;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record CheckoutItemVm(
        Long id,
        Long productId,
        String productName,
        String description,
        int quantity,
        BigDecimal productPrice,
        BigDecimal taxAmount,
        BigDecimal discountAmount,
        BigDecimal shipmentFee,
        BigDecimal shipmentTax,
        String checkoutId,
        Long taxClassId,
        DimensionUnit dimensionUnit,
        Double weight,
        Double length,
        Double width,
        Double height) {
}
