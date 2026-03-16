package com.yas.commonlibrary.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class AbstractAuditEntityTest {

    @Test
    void gettersAndSetters_WorkAsExpected() {
        TestAuditEntity entity = new TestAuditEntity();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime later = now.plusHours(1);

        entity.setCreatedOn(now);
        entity.setCreatedBy("creator");
        entity.setLastModifiedOn(later);
        entity.setLastModifiedBy("editor");

        assertEquals(now, entity.getCreatedOn());
        assertEquals("creator", entity.getCreatedBy());
        assertEquals(later, entity.getLastModifiedOn());
        assertEquals("editor", entity.getLastModifiedBy());
    }

    private static class TestAuditEntity extends AbstractAuditEntity {
    }
}
