package com.yas.cart.controller;

import com.yas.cart.service.CartItemV2Service;
import com.yas.cart.viewmodel.CartItemV2GetVm;
import com.yas.cart.viewmodel.CartItemV2PostVm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CartItemV2Controller {
    private final CartItemV2Service cartItemService;

    @PostMapping("/storefront/cart/items")
    public ResponseEntity<CartItemV2GetVm> addCartItem(@Valid @RequestBody CartItemV2PostVm cartItemPostVm) {
        CartItemV2GetVm cartItemGetVm = cartItemService.addCartItem(cartItemPostVm);
        return ResponseEntity.ok(cartItemGetVm);
    }
}
