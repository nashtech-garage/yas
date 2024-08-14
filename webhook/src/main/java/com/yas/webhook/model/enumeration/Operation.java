package com.yas.webhook.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Operation {

    UPDATE("u"),
    CREATE("c"),
    DELETE("d"),
    READ("r");

    final String name;
}
