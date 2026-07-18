package com.yas.recommendation.vector.common.document;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class DefaultIdGeneratorTest {

    @Test
    void generateId_shouldReturnId() {
        DefaultIdGenerator generator = new DefaultIdGenerator("prefix", 1L);
        String id = generator.generateId();
        assertNotNull(id);
    }
}
