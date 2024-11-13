package com.yas.recommendation.chat.enumeration;

public enum OrderStatus {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    PENDING_PAYMENT("PENDING_PAYMENT"),
    PAID("PAID"),
    SHIPPING("SHIPPING"),
    COMPLETED("COMPLETED"),
    REFUND("REFUND"),
    CANCELLED("CANCELLED"),

    REJECT("REJECT");

    private final String name;

    OrderStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
