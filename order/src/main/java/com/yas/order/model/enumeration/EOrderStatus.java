package com.yas.order.model.enumeration;

public enum EOrderStatus {
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

    EOrderStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
