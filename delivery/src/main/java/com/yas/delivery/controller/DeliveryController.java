package com.yas.delivery.controller;

import com.yas.delivery.service.DeliveryService;
import com.yas.delivery.viewmodel.CalculateFeesPostVm;
import com.yas.delivery.viewmodel.DeliveryFeeVm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping("/storefront/shipment/calculate")
    public ResponseEntity<DeliveryFeeVm> calculateDeliveryFee(
        @Valid @RequestBody CalculateFeesPostVm calculateFeePostVm) {
        return ResponseEntity.ok(deliveryService.calculateDeliveryFee(calculateFeePostVm));
    }
}
