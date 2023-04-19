package com.yas.order.controller;

import com.yas.order.service.PayPalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/paypal")
public class PayPalController {

    private final PayPalService payPalClient;
    @Autowired
    PayPalController(PayPalService payPalClient){
        this.payPalClient = payPalClient;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping(value = "/make/payment")
    public Map<String, Object> makePayment(@RequestParam("sum") String sum){
        return payPalClient.createPayment(sum);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping(value = "/complete/payment")
    public Map<String, Object> completePayment(HttpServletRequest request, @RequestParam("paymentId") String paymentId, @RequestParam("payerId") String payerId){
        return payPalClient.completePayment(request);
    }


}