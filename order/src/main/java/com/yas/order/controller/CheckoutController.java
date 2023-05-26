package com.yas.order.controller;

import com.yas.order.service.CheckoutService;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping("/storefront/checkouts")
    public ResponseEntity<CheckoutVm> createCheckout(@Valid @RequestBody CheckoutPostVm checkoutPostVm) {
        return ResponseEntity.ok(checkoutService.createCheckout(checkoutPostVm));
    }

    @GetMapping("/storefront/checkouts/{id}")
    public ResponseEntity<CheckoutVm> getOrderWithItemsById(@PathVariable String id) {
        return ResponseEntity.ok(checkoutService.getCheckoutPendingStateWithItemsById(id));
    }
}
