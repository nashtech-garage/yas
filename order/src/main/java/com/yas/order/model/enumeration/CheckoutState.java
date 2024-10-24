package com.yas.order.model.enumeration;

public enum CheckoutState {
    COMPLETED("Completed"),
    PENDING("Pending"),
    LOCK("LOCK"),
    CHECKED_OUT("Checked Out"),
    PAYMENT_PROCESSING("Payment Processing"),
    PAYMENT_FAILED("Payment Failed"),
    PAYMENT_CONFIRMED("Payment Confirmed"),
    FULFILLED("Fulfilled")
    ;
    private final String name;

    CheckoutState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
