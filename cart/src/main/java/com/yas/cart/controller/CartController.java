package com.yas.cart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.yas.cart.service.CartService;
import com.yas.cart.viewmodel.*;

@RestController
public class CartController {
    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/backoffice/carts")
    public ResponseEntity<List<CartListVM>> listCarts() {
        return ResponseEntity.ok(cartService.getCarts());
    }
    
    @GetMapping("/storefront/carts/{customerID}")
    public ResponseEntity<List<CartGetDetailVM>> listCartDetailByCustomerID(@PathVariable String customerID) {
        return ResponseEntity.ok(cartService.getCartDetailByCustomerID(customerID));
    }
}
