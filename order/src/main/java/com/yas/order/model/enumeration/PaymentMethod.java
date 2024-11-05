package com.yas.order.model.enumeration;

public enum PaymentMethod {
    COD, BANKING, PAYPAL;

    public static PaymentMethod fromValue(String value) {
        try {
            return PaymentMethod.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment method: " + value);
        }
    }
}

