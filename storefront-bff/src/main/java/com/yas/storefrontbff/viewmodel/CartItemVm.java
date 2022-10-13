package com.yas.storefrontbff.viewmodel;


public record CartItemVm(Long productId, int quantity) {
    public static CartItemVm fromCartDetailVm(CartDetailVm cartDetailVm) {
        return new CartItemVm(cartDetailVm.productId(), cartDetailVm.quantity());
    }
}
