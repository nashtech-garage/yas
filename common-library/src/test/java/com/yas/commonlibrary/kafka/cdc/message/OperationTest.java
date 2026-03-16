package com.yas.commonlibrary.kafka.cdc.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class OperationTest {

    @Test
    void getName_ReturnsConfiguredValues() {
        assertEquals("r", Operation.READ.getName());
        assertEquals("c", Operation.CREATE.getName());
        assertEquals("u", Operation.UPDATE.getName());
        assertEquals("d", Operation.DELETE.getName());
    }
}
