package com.yas.delivery.service;

import org.springframework.stereotype.Service;

@Service
public class DeliveryService {
    public String getStatus() {
        return "Delivery Service is up and running";
    }
}
