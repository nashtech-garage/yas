package com.yas.order.viewmodel;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorVmTest {

    @Test
    void constructorWithoutFieldErrorsShouldCreateEmptyList() {
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Invalid input");

        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad Request", errorVm.title());
        assertEquals("Invalid input", errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void constructorWithFieldErrorsShouldKeepProvidedList() {
        List<String> fieldErrors = List.of("product is required", "quantity must be positive");

        ErrorVm errorVm = new ErrorVm("422", "Validation Error", "Invalid fields", fieldErrors);

        assertEquals("422", errorVm.statusCode());
        assertEquals("Validation Error", errorVm.title());
        assertEquals("Invalid fields", errorVm.detail());
        assertEquals(fieldErrors, errorVm.fieldErrors());
        assertEquals(2, errorVm.fieldErrors().size());
    }
}
