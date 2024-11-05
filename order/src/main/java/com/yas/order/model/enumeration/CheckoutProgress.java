package com.yas.order.model.enumeration;

public enum CheckoutProgress {
    INIT("Init"),
    PROMOTION_CODE_APPLIED("Promotion code applied"),
    PROMOTION_CODE_APPLIED_FAILED("Promotion Code applied failed"),
    STOCK_LOCKED("Stock locked"),
    STOCK_LOCKED_FAILED("Stock locked failed"),
    PAYMENT_CREATED("Payment created"),
    PAYMENT_CREATED_FAILED("Payment created failed");
    private final String name;

    CheckoutProgress(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
