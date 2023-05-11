package com.yas.order.model.enumeration;

public enum ECheckoutState {
    COMPLETED("Completed"), PENDING("Pending");
    private final String name;

    ECheckoutState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
