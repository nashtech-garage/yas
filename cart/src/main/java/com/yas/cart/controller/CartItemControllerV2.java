package com.yas.cart.controller;

import com.yas.cart.service.CartItemServiceV2;
import com.yas.cart.viewmodel.CartItemGetVmV2;
import com.yas.cart.viewmodel.CartItemPostVmV2;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CartItemControllerV2 {
    private final CartItemServiceV2 cartItemService;

    @PostMapping("/storefront/cart/items")
    public ResponseEntity<CartItemGetVmV2> addCartItem(@Valid @RequestBody CartItemPostVmV2 cartItemPostVm) {
        CartItemGetVmV2 cartItemGetVm = cartItemService.addCartItem(cartItemPostVm);
        return ResponseEntity.ok(cartItemGetVm);
    }
}
