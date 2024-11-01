package com.yas.order.mapper;

import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.viewmodel.checkout.CheckoutItemPostVm;
import com.yas.order.viewmodel.checkout.CheckoutItemVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;


import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface CheckoutMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checkout", ignore = true)
    CheckoutItem toModel(CheckoutItemPostVm checkoutItemPostVm);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checkoutState", ignore = true)
    @Mapping(target = "totalAmount", source = "totalAmount") // Ánh xạ tường minh cho totalAmount
    @Mapping(target = "totalDiscountAmount", source = "totalDiscountAmount")
    Checkout toModel(CheckoutPostVm checkoutPostVm);

    CheckoutItemVm toVm(CheckoutItem checkoutItem);

    @Mapping(target = "checkoutItemVms", ignore = true)
    CheckoutVm toVm(Checkout checkout);

    default BigDecimal map(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
