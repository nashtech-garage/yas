package com.yas.commonlibrary.kafka.cdc.message;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Operation {

    READ("r"),
    CREATE("c"),
    UPDATE("u"),
    DELETE("d");

    private final String name;

    Operation(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
