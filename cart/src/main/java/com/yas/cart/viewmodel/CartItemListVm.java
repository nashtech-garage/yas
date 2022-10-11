package com.yas.cart.viewmodel;

import java.util.List;

public record CartItemListVm(List<CartItemVm> cartItemVmList) {

    public static Long getProductId(CartItemVm cartItemVm) {
        return cartItemVm.productId();
    }
    public static Integer getproductQuantity(CartItemVm cartItemVm) {
        return cartItemVm.quantity();
    }

}
