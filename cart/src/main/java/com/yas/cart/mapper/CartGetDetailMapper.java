package com.yas.cart.mapper;

import com.yas.cart.model.Cart;
import com.yas.cart.viewmodel.CartGetDetailVm;
import com.yas.commonlibrary.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CartGetDetailMapper extends BaseMapper<Cart, CartGetDetailVm> {

    @Mapping(target = "cartDetails", source = "cartItems")
    @Override
    CartGetDetailVm toVm(Cart cart);

}
