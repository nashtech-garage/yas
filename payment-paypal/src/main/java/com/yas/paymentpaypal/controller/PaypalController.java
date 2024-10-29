package com.yas.paymentpaypal.controller;

import com.yas.paymentpaypal.service.PaypalService;
import com.yas.paymentpaypal.viewmodel.CapturedPaymentVm;
import com.yas.paymentpaypal.viewmodel.PaymentPaypalRequest;
import com.yas.paymentpaypal.viewmodel.PaymentPaypalResponse;
import com.yas.paymentpaypal.viewmodel.PaypalRequestPayment;
import com.yas.paymentpaypal.viewmodel.RequestPayment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/events/checkout/orders")
    public ResponseEntity<PaymentPaypalResponse> createOrderOnPaypal(
        @Valid @RequestBody PaymentPaypalRequest paymentPaypalRequest
    ) {
        PaymentPaypalResponse response = paypalService.createOrderOnPaypal(paymentPaypalRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}