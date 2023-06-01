package com.yas.order.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public enum EOrderStatus {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    PENDING_PAYMENT("PENDING_PAYMENT"),
    SHIPPING("SHIPPING"),
    COMPLETED("COMPLETED"),
    REFUND("REFUND"),
    CANCELLED("CANCELLED");

    private final String name;

    EOrderStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
