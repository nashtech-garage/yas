package com.yas.payment.controller;

import com.yas.payment.model.PaymentProvider;
import com.yas.payment.repository.PaymentProviderRepository;
import com.yas.payment.service.PaymentProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment-providers")
public class PaymentProviderController {
    private final PaymentProviderService paymentProviderService;

    @GetMapping("/{id}/additional-settings")
    public ResponseEntity<String> getAdditionalSettings(@PathVariable("id") String id) {
        return ResponseEntity.ok(paymentProviderService
                .getAdditionalSettingsByPaymentProviderId(id));
    }
}