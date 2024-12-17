package com.yas.order.viewmodel.enumeration;

import lombok.Getter;

@Getter
public enum DimensionUnit {
    CM("cm"),
    INCH("inch");

    private final String name;

    DimensionUnit(String name) {
        this.name = name;
    }

}