package com.yas.order.viewmodel.enumeration;

import lombok.Getter;

@Getter
public enum CheckoutAddressType {
    SHIPPING("shipping"),
    BILLING("billing");

    private final String name;

    CheckoutAddressType(String name) {
        this.name = name;
    }
}
