package com.yas.order.mapper;

import com.yas.order.model.CheckoutAddress;
import com.yas.order.viewmodel.customer.ActiveAddressVm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface AddressMapper {
    @Mapping(target = "id", ignore = true)
    CheckoutAddress toModel(ActiveAddressVm activeAddressVm);

    ActiveAddressVm toVm(CheckoutAddress checkoutAddress);

    @Mapping(target = "id", ignore = true)
    CheckoutAddress updateModel(@MappingTarget CheckoutAddress checkoutAddress, ActiveAddressVm activeAddressVm);
}
