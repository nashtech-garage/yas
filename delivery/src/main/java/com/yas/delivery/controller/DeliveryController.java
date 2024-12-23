package com.yas.delivery.controller;

import com.yas.delivery.service.DeliveryService;
import com.yas.delivery.viewmodel.CalculateDeliveryFeeVm;
import com.yas.delivery.viewmodel.DeliveryFeeVm;
import com.yas.delivery.viewmodel.DeliveryProviderVm;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @GetMapping("/storefront/delivery/providers")
    public ResponseEntity<List<DeliveryProviderVm>> getDeliveryProviders() {
        return ResponseEntity.ok(deliveryService.getDeliveryProviders());
    }

    @PostMapping("/storefront/delivery/calculate")
    public ResponseEntity<DeliveryFeeVm> calculateDeliveryFee(
        @Valid @RequestBody CalculateDeliveryFeeVm calculateDeliveryFeeVm) {
        return ResponseEntity.ok(deliveryService.calculateDeliveryFee(calculateDeliveryFeeVm));
    }
}
