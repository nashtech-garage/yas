package com.yas.cart.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<CartGetDetailVM>> listCartDetailByCustomerID(@PathVariable String customerID, Principal principal, HttpServletRequest request) {
        // Only admin or the owner of the cart can access.
        if(principal != null && (principal.getName().equals(customerID) || request.isUserInRole("ADMIN")))
            return ResponseEntity.ok(cartService.getCartDetailByCustomerID(customerID));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}
