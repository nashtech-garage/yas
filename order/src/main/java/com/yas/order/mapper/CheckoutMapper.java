package com.yas.order.mapper;

import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.viewmodel.checkout.CheckoutItemPostVm;
import com.yas.order.viewmodel.checkout.CheckoutItemVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface CheckoutMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checkout", ignore = true)
    CheckoutItem toModel(CheckoutItemPostVm checkoutItemPostVm);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checkoutState", ignore = true)
    @Mapping(target = "progress", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "shipmentMethodId", ignore = true)
    @Mapping(target = "paymentMethodId", ignore = true)
    @Mapping(target = "shippingAddressId", ignore = true)
    @Mapping(target = "lastError", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    @Mapping(target = "totalShipmentFee", ignore = true)
    @Mapping(target = "totalShipmentTax", ignore = true)
    @Mapping(target = "totalTax", ignore = true)
    
    Checkout toModel(CheckoutPostVm checkoutPostVm);

    @Mapping(target = "checkoutId", source = "checkout.id")
    CheckoutItemVm toVm(CheckoutItem checkoutItem);

    @Mapping(target = "checkoutItemVms", ignore = true)
    CheckoutVm toVm(Checkout checkout);

    default BigDecimal map(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
