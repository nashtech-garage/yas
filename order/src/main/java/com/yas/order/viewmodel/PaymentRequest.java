package com.yas.order.viewmodel;

import com.paypal.api.payments.Payer;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;

import java.util.ArrayList;
import java.util.List;

public record PaymentRequest(
        Double price, String currency, String method, String intent, String description) {

}