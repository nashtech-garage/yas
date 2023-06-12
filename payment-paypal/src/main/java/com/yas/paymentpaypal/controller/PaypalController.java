package com.yas.paymentpaypal.controller;

import com.yas.paymentpaypal.service.PaypalService;
import com.yas.paymentpaypal.viewmodel.CapturedPaymentVm;
import com.yas.paymentpaypal.viewmodel.PaypalRequestPayment;
import com.yas.paymentpaypal.viewmodel.RequestPayment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaypalController {
    private final PaypalService paypalService;

    @PostMapping(value = "/init")
    public PaypalRequestPayment createPayment(@Valid @RequestBody RequestPayment requestPayment) {
        return paypalService.createPayment(requestPayment);
    }

    @GetMapping(value = "/capture")
    public CapturedPaymentVm capturePayment(@RequestParam("token") String token) {
        return paypalService.capturePayment(token);
    }

    @GetMapping(value = "/cancel")
    public ResponseEntity<String> cancelPayment() {
        return ResponseEntity.ok("Payment cancelled");
    }
}