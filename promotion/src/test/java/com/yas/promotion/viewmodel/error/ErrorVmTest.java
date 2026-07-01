package com.yas.promotion.viewmodel.error;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorVmTest {

    @Test
    void testConstructorWithAllFields() {
        List<String> fieldErrors = List.of("error1", "error2");
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Detail message", fieldErrors);

        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad Request", errorVm.title());
        assertEquals("Detail message", errorVm.detail());
        assertEquals(fieldErrors, errorVm.fieldErrors());
    }

    @Test
    void testConstructorWithoutFieldErrors() {
        ErrorVm errorVm = new ErrorVm("500", "Internal Server Error", "Something went wrong");

        assertEquals("500", errorVm.statusCode());
        assertEquals("Internal Server Error", errorVm.title());
        assertEquals("Something went wrong", errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }
}
