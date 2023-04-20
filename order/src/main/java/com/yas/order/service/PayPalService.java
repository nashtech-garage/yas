package com.yas.order.service;


import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class PayPalService {
    @Autowired
    private APIContext apiContext;

    public Payment createPayment(Double total, String currency, String method,
                                 String intent, String description, String cancelUrl, String successUrl) throws PayPalRESTException {
        Amount theAmount = new Amount();
        theAmount.setCurrency(currency);
        total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
        theAmount.setTotal(String.format("%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(theAmount);

        List<Transaction> theTransactions = new ArrayList<>();
        theTransactions.add(transaction);

        Payer thePayer = new Payer();
        thePayer.setPaymentMethod(method.toString());

        RedirectUrls theRedirectUrls = new RedirectUrls();
        theRedirectUrls.setCancelUrl(cancelUrl);
        theRedirectUrls.setReturnUrl(cancelUrl);

        Payment thePayment = new Payment();
        thePayment.setIntent(intent);
        thePayment.setTransactions(theTransactions);
        thePayment.setPayer(thePayer);
        thePayment.setRedirectUrls(theRedirectUrls);

        return thePayment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {

        Payment thePayment = new Payment();
        thePayment.setId(paymentId);

        PaymentExecution thPaymentExecution = new PaymentExecution();
        thPaymentExecution.setPayerId(payerId);

        return thePayment.execute(apiContext, thPaymentExecution);
    }
}