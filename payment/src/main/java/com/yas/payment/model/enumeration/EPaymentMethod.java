package com.yas.payment.model.enumeration;

public enum EPaymentMethod {
    COD("On-Delivery Payment"), BANKING("Banking"), PAYPAL("Payment with Paypal");
    private final String name;

    EPaymentMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}