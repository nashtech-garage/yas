package com.yas.order.controller;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.yas.order.service.PayPalService;
import com.yas.order.viewmodel.CompletedOrder;
import com.yas.order.viewmodel.PaymentOrder;
import com.yas.order.viewmodel.PaymentRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/paypal")
public class PayPalController {
    @Autowired
    private PayPalService paypalService;

    @PostMapping(value = "/init")
    public PaymentOrder createPayment(
            @RequestParam("sum") BigDecimal sum) {
        return paypalService.createPayment(sum);
    }

    @PostMapping(value = "/capture")
    public CompletedOrder completePayment(@RequestParam("token") String token) {
        return paypalService.completePayment(token);
    }
}