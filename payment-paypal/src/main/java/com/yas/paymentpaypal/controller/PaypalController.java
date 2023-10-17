package com.yas.paymentpaypal.controller;

import com.yas.paymentpaypal.service.PaypalService;
import com.yas.paymentpaypal.viewmodel.CapturedPaymentVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaypalController {
    private final PaypalService paypalService;

    @GetMapping(value = "/capture")
    public CapturedPaymentVm capturePayment(@RequestParam("token") String token) {
        return paypalService.capturePayment(token);
    }

    @GetMapping(value = "/cancel")
    public ResponseEntity<String> cancelPayment() {
        return ResponseEntity.ok("Payment cancelled");
    }
}