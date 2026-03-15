package com.yas.promotion.validation;

import org.junit.jupiter.api.Test;
import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromotionConstraintTest {

    @Test
    void testPromotionConstraintDefaults() {
        // Retrieve the annotation from a dummy class
        PromotionConstraint constraint = DummyClass.class.getAnnotation(PromotionConstraint.class);
        assertNotNull(constraint);
        assertEquals("Promotion is invalid", constraint.message());
        assertEquals(0, constraint.groups().length);
        assertEquals(0, constraint.payload().length);
    }

    @PromotionConstraint
    private static class DummyClass {
    }
}
