package com.yas.payment.viewmodel;

import lombok.Builder;

@Builder
public record InitPaymentResponseVm(String status, String paymentId, String redirectUrl) {
}
