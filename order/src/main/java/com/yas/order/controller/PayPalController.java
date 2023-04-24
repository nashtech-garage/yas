package com.yas.order.controller;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.yas.order.service.PayPalService;
import com.yas.order.viewmodel.PaymentRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/paypal")
public class PayPalController {
    @Autowired
    private PayPalService paypalService;

    public static final String successUrl = "http://localhost:8085/order/paypal/success";
    public static final String cancelUrl = "http://localhost:8085/order/paypal/cancel";

    @PostMapping("/pay")
    public String payment(@Valid @RequestBody  PaymentRequest request) throws PayPalRESTException {

        Payment thePayment = paypalService.createPayment(request.price(), request.currency(),
                request.method(), request.intent(), request.description(), cancelUrl, successUrl);
        for (Links links: thePayment.getLinks()){
            if(links.getRel().equals("approval_url")){
                return links.getHref();
            }
        }
        return null;
    }

    @GetMapping("/success")
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            System.out.println(payment.toJSON());
            if (payment.getState().equals("approved")) {
                return "success";
            }
        } catch (PayPalRESTException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> cancelPayment() {
        return ResponseEntity.ok("Payment cancelled");
    }
}