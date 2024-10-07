package com.yas.order.controller;

import com.yas.order.service.CartItemService;
import com.yas.order.viewmodel.cart.CartItemGetVm;
import com.yas.order.viewmodel.cart.CartItemPostVm;
import com.yas.order.viewmodel.cart.CartItemPutVm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @PostMapping("/storefront/cart/items")
    public void addCartItem(@Valid @RequestBody CartItemPostVm cartItemPostVm) {
        cartItemService.addCartItem(cartItemPostVm);
    }

    @PutMapping("/storefront/cart/items/{productId}")
    public CartItemGetVm updateCartItem(@PathVariable Long productId,
                                        @Valid @RequestBody CartItemPutVm cartItemPutVm) {
        return cartItemService.updateCartItem(productId, cartItemPutVm);
    }
}
