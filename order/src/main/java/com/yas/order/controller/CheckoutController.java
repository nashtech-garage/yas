package com.yas.order.controller;

import com.yas.order.service.CheckoutService;
import com.yas.order.viewmodel.checkout.CheckoutPostVm;
import com.yas.order.viewmodel.checkout.CheckoutStatusPutVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping("/storefront/checkouts")
    public ResponseEntity<CheckoutVm> createCheckout(@Valid @RequestBody CheckoutPostVm checkoutPostVm) {
        return ResponseEntity.ok(checkoutService.createCheckout(checkoutPostVm));
    }

    @PutMapping("/storefront/checkouts/status")
    public ResponseEntity<Long> updateCheckoutStatus(@Valid @RequestBody CheckoutStatusPutVm checkoutStatusPutVm) {
        return ResponseEntity.ok(checkoutService.updateCheckoutStatus(checkoutStatusPutVm));
    }

    @GetMapping("/storefront/checkouts/pending/{id}")
    public ResponseEntity<CheckoutVm> getPendingCheckoutDetailsById(@PathVariable String id) {
        return ResponseEntity.ok(checkoutService.getCheckoutPendingStateWithItemsById(id));
    }

    @GetMapping("/storefront/checkouts/{id}")
    public ResponseEntity<CheckoutVm> getCheckoutById(@PathVariable String id) {
        return ResponseEntity.ok(checkoutService.findCheckoutWithItemsById(id));
    }
}
