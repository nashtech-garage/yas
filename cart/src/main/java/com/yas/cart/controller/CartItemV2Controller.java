package com.yas.cart.controller;

import com.yas.cart.service.CartItemV2Service;
import com.yas.cart.viewmodel.CartItemV2DeleteVm;
import com.yas.cart.viewmodel.CartItemV2GetVm;
import com.yas.cart.viewmodel.CartItemV2PostVm;
import com.yas.cart.viewmodel.CartItemV2PutVm;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PutMapping("/storefront/cart/items/{productId}")
    public ResponseEntity<CartItemV2GetVm> updateCartItem(@PathVariable Long productId,
                                                          @Valid @RequestBody CartItemV2PutVm cartItemPutVm) {
        CartItemV2GetVm cartItemGetVm = cartItemService.updateCartItem(productId, cartItemPutVm);
        return ResponseEntity.ok(cartItemGetVm);
    }

    @GetMapping("/storefront/cart/items")
    public ResponseEntity<List<CartItemV2GetVm>> getCartItems() {
        List<CartItemV2GetVm> cartItemGetVms = cartItemService.getCartItems();
        return ResponseEntity.ok(cartItemGetVms);
    }

    @PostMapping("/storefront/cart/items/remove")
    public ResponseEntity<List<CartItemV2GetVm>> removeCartItems(
        @RequestBody List<@Valid CartItemV2DeleteVm> cartItemDeleteVms) {
        List<CartItemV2GetVm> cartItemGetVms = cartItemService.deleteOrAdjustCartItem(cartItemDeleteVms);
        return ResponseEntity.ok(cartItemGetVms);
    }

    @DeleteMapping("/storefront/cart/items/{productId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long productId) {
        cartItemService.deleteCartItem(productId);
        return ResponseEntity.noContent().build();
    }
}