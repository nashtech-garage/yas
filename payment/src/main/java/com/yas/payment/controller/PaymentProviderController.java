package com.yas.payment.controller;

import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentProviderController {

    private final PaymentProviderService paymentProviderService;

    public PaymentProviderController(PaymentProviderService paymentProviderService) {
        this.paymentProviderService = paymentProviderService;
    }

    @PostMapping("/backoffice/payment-providers")
    public ResponseEntity<PaymentProviderVm> create(@Valid @RequestBody CreatePaymentVm createPaymentVm) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(paymentProviderService.create(createPaymentVm));
    }

   @PutMapping("/backoffice/payment-providers")
    public ResponseEntity<PaymentProviderVm> update(@Valid @RequestBody UpdatePaymentVm updatePaymentVm) {
        return ResponseEntity.ok(paymentProviderService.update(updatePaymentVm));
    }

    @GetMapping("/storefront/payment-providers")
    public ResponseEntity<List<PaymentProviderVm>> getAll(Pageable pageable) {
        var paymentProviders = paymentProviderService.getEnabledPaymentProviders(pageable);
        return ResponseEntity.ok(paymentProviders);
    }

}