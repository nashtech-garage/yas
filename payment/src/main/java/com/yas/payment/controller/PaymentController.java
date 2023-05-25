package com.yas.payment.controller;

import com.yas.payment.model.Payment;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturedPayment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/capture")
    public ResponseEntity<CapturedPayment> capturePayment(@Valid @RequestBody CapturedPayment capturedPayment) {
        return ResponseEntity.ok(paymentService.capturePayment(capturedPayment));
    }
}
