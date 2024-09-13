package com.yas.order.mapper;

import com.yas.order.model.Checkout;
import com.yas.order.model.CheckoutItem;
import com.yas.order.viewmodel.checkout.CheckoutItemPostVm;
import com.yas.order.viewmodel.checkout.CheckoutItemVm;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface CheckoutMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checkoutId", ignore = true)
    CheckoutItem toModel(CheckoutItemPostVm checkoutItemPostVm);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checkoutState", ignore = true)
    @Mapping(target = "checkoutItem", source = "checkoutItemPostVms")
    Checkout toModel(CheckoutPostVm checkoutPostVm);

    @Mapping(target = "checkoutId", source = "checkoutId.id")
    CheckoutItemVm toVm(CheckoutItem checkoutItem);

    @Mapping(target = "checkoutItemVms", source = "checkoutItem")
    CheckoutVm toVm(Checkout checkout);
}
