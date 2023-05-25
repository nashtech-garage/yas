package com.yas.paymentpaypal.controller;

import com.yas.paymentpaypal.service.PaypalService;
import com.yas.paymentpaypal.viewmodel.PaypalRequestPayment;
import com.yas.paymentpaypal.viewmodel.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class PaypalController {
    private final PaypalService paypalService;

    @PostMapping(value = "/init")
    public PaypalRequestPayment createPayment(
            @RequestParam("totalPrice") BigDecimal sum) {
        return paypalService.createPayment(sum);
    }

    @GetMapping(value = "/capture")
    public PaymentResponse completePayment(@RequestParam("token") String token) {
        return paypalService.completePayment(token);
    }

    @GetMapping(value = "/cancel")
    public ResponseEntity<String> cancelPayment() {
        return ResponseEntity.ok("Payment cancelled");
    }
}