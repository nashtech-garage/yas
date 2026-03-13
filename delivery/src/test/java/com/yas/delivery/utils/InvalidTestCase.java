package com.yas.delivery.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvalidTestCase<T> {
    private String description;
    private T input;

    @Override
    public String toString() {
        return description;
    }
}
