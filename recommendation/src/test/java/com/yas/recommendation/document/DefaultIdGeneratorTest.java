package com.yas.recommendation.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.recommendation.vector.common.document.DefaultIdGenerator;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DefaultIdGeneratorTest {

    @Test
    void generateId_shouldReturnValidUUID() {
        DefaultIdGenerator generator = new DefaultIdGenerator("product", 1L);
        String id = generator.generateId();
        assertThat(id).isNotNull();
        // Must be valid UUID format
        assertThat(UUID.fromString(id)).isNotNull();
    }

    @Test
    void generateId_sameInputSamePrefix_shouldReturnSameId() {
        DefaultIdGenerator g1 = new DefaultIdGenerator("product", 42L);
        DefaultIdGenerator g2 = new DefaultIdGenerator("product", 42L);
        assertThat(g1.generateId()).isEqualTo(g2.generateId());
    }

    @Test
    void generateId_differentId_shouldReturnDifferentUUID() {
        DefaultIdGenerator g1 = new DefaultIdGenerator("product", 1L);
        DefaultIdGenerator g2 = new DefaultIdGenerator("product", 2L);
        assertThat(g1.generateId()).isNotEqualTo(g2.generateId());
    }

    @Test
    void generateId_differentPrefix_shouldReturnDifferentUUID() {
        DefaultIdGenerator g1 = new DefaultIdGenerator("product", 1L);
        DefaultIdGenerator g2 = new DefaultIdGenerator("category", 1L);
        assertThat(g1.generateId()).isNotEqualTo(g2.generateId());
    }
}
