package com.yas.payment.controller;

import com.yas.payment.service.PayPalService;
import com.yas.payment.viewmodel.paypal.CompletedOrder;
import com.yas.payment.viewmodel.paypal.PaypalOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/paypal")
public class PayPalController {
    @Autowired
    private PayPalService paypalService;

    @PostMapping(value = "/init")
    public PaypalOrder createPayment(
            @RequestParam("sum") BigDecimal sum) {
        return paypalService.createPayment(sum);
    }

    @GetMapping(value = "/capture")
    public CompletedOrder completePayment(@RequestParam("token") String token) {
        return paypalService.completePayment(token);
    }

    @GetMapping(value = "/cancel")
    public ResponseEntity<String> cancelPayment() {
        return ResponseEntity.ok("Payment cancelled");
    }
}