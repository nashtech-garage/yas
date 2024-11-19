package com.yas.order.model.enumeration;

public enum OrderStatus {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    PENDING_PAYMENT("PENDING_PAYMENT"),
    PAID("PAID"),
    SHIPPING("SHIPPING"),
    COMPLETED("COMPLETED"),
    REFUND("REFUND"),
    REJECT("REJECT"),

    PAYMENT_CONFIRMED("PAYMENT_CONFIRMED"),
    READY_TO_SHIP("READY_TO_SHIP"),
    SHIPPED("SHIPPED"),
    CANCEL_REQUESTED("CANCEL_REQUESTED"),
    RECEIVED("RECEIVED"),
    SHIPMENT_FAILED("SHIPMENT_FAILED"),
    REFUND_PROCESSING("REFUND_PROCESSING"),
    CONFIRMED("CONFIRMED"),
    CANCELLED("CANCELLED");

    private final String name;

    OrderStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
