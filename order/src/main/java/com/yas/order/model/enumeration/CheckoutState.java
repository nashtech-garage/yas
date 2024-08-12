package com.yas.order.model.enumeration;

public enum CheckoutState {
    COMPLETED("Completed"), PENDING("Pending"), LOCK("LOCK");
    private final String name;

    CheckoutState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
