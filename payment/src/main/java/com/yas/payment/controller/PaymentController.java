package com.yas.payment.controller;

import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturedPayment;
import com.yas.payment.viewmodel.PaymentOrderStatusVm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/storefront/payments/capture")
    public ResponseEntity<PaymentOrderStatusVm> capturePayment(@Valid @RequestBody CapturedPayment capturedPayment) {
        PaymentOrderStatusVm paymentOrderStatusVm = paymentService.capturePayment(capturedPayment);
        return ResponseEntity.ok(paymentOrderStatusVm);
    }
}
