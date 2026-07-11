package com.yas.delivery.controller;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;
import com.yas.delivery.service.DeliveryService;

@RestController
public class DeliveryController {
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/status")
    public String getStatus() {
        return deliveryService.getStatus();
    }
}
