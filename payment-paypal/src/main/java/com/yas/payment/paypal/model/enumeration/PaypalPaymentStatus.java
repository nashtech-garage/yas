package com.yas.payment.paypal.model.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.yas.commonlibrary.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum PaypalPaymentStatus {

    PAYMENT_AUTHORIZATION_CREATED("PAYMENT.AUTHORIZATION.CREATED"),
    PAYMENT_AUTHORIZATION_VOIDED("PAYMENT.AUTHORIZATION.VOIDED"),
    PAYMENT_CAPTURE_DECLINED("PAYMENT.CAPTURE.DECLINED"),
    PAYMENT_CAPTURE_COMPLETED("PAYMENT.CAPTURE.COMPLETED"),
    PAYMENT_CAPTURE_PENDING("PAYMENT.CAPTURE.PENDING"),
    PAYMENT_CAPTURE_REFUNDED("PAYMENT.CAPTURE.REFUNDED"),
    PAYMENT_CAPTURE_REVERSED("PAYMENT.CAPTURE.REVERSED");

    private final String status;

    PaypalPaymentStatus(String status) {
        this.status = status;
    }

    @JsonCreator
    public static PaypalPaymentStatus fromString(String status) {
        for (PaypalPaymentStatus paymentStatus : PaypalPaymentStatus.values()) {
            if (paymentStatus.getStatus().equals(status)) {
                return paymentStatus;
            }
        }
        throw new BadRequestException("Unknown enum value: {}", status);
    }

    @JsonValue
    public String getStatus() {
        return status;
    }
}
