package com.yas.payment.controller;

import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturePaymentRequest;
import com.yas.payment.viewmodel.CapturePaymentResponse;
import com.yas.payment.viewmodel.InitPaymentRequest;
import com.yas.payment.viewmodel.InitPaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(value = "/init")
    public InitPaymentResponse initPayment(@Valid @RequestBody InitPaymentRequest initPaymentRequest) {
        return paymentService.initPayment(initPaymentRequest);
    }

    @PostMapping(value = "/capture")
    public CapturePaymentResponse capturePayment(@Valid @RequestBody CapturePaymentRequest capturePaymentRequest) {
        return paymentService.capturePayment(capturePaymentRequest);
    }

    @GetMapping(value = "/cancel")
    public ResponseEntity<String> cancelPayment() {
        return ResponseEntity.ok("Payment cancelled");
    }
}
