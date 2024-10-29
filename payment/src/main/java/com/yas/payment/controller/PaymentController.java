package com.yas.payment.controller;

import com.yas.payment.model.Payment;
import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturedPayment;
import com.yas.payment.viewmodel.CheckoutPaymentVm;
import com.yas.payment.viewmodel.PaymentOrderStatusVm;
import com.yas.payment.viewmodel.PaymentVm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/events/payments")
    public ResponseEntity<Long> createPaymentFromEvent(
        @Valid @RequestBody CheckoutPaymentVm checkoutPaymentRequestDto
    ) {

        Long paymentId = paymentService.createPaymentFromEvent(checkoutPaymentRequestDto);
        return new ResponseEntity<>(paymentId, HttpStatus.CREATED);
    }

    @GetMapping("/storefront/payments/{id}")
    public ResponseEntity<PaymentVm> getCheckoutById(@PathVariable Long id) {
        Payment payment = paymentService.findPaymentById(id);
        return ResponseEntity.ok(PaymentVm.fromModel(payment));
    }
}
