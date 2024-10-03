package com.yas.order.controller;

import com.yas.order.service.CartItemService;
import com.yas.order.viewmodel.cart.CartItemGetVm;
import com.yas.order.viewmodel.cart.CartItemPostVm;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @GetMapping("/storefront/cart/items")
    public List<CartItemGetVm> getCartItems() {
        return cartItemService.getCartItems();
    }

    @PostMapping("/storefront/cart/items")
    public void addCartItem(@Valid @RequestBody CartItemPostVm cartItemPostVm) {
        cartItemService.addCartItem(cartItemPostVm);
    }
}
