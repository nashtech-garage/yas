package com.yas.payment.paypal.viewmodel;

import lombok.Builder;

@Builder
public record PaypalCreatePaymentResponse(String status, String paymentId, String redirectUrl) {
}