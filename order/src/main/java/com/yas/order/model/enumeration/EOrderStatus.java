package com.yas.order.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Getter
public enum EOrderStatus {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    PENDING_PAYMENT("PENDING_PAYMENT"),
    SHIPPING("SHIPPING"),
    COMPLETED("COMPLETED"),
    REFUND("REFUND"),
    CANCELLED("CANCELLED");

    private final String value;

    private static final Map<String, EOrderStatus> map;

    static {
        Map<String, EOrderStatus> localMap = new ConcurrentHashMap<>();
        for (EOrderStatus value : EOrderStatus.values()) {
            localMap.put(value.value, value);
        }
        map = Collections.unmodifiableMap(localMap);
    }

    public static EOrderStatus of(String value) {

        return map.get(value);
    }
}
