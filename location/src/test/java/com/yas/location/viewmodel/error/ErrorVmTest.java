package com.yas.location.viewmodel.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void testErrorVmWithThreeArgs_Success() {
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Invalid input");

        assertNotNull(errorVm);
        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad Request", errorVm.title());
        assertEquals("Invalid input", errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void testErrorVmWithFourArgs_Success() {
        List<String> fieldErrors = new ArrayList<>();
        fieldErrors.add("field1 is required");
        fieldErrors.add("field2 must be positive");

        ErrorVm errorVm = new ErrorVm("400", "Validation Error", "Validation failed", fieldErrors);

        assertNotNull(errorVm);
        assertEquals("400", errorVm.statusCode());
        assertEquals("Validation Error", errorVm.title());
        assertEquals("Validation failed", errorVm.detail());
        assertEquals(2, errorVm.fieldErrors().size());
        assertTrue(errorVm.fieldErrors().contains("field1 is required"));
        assertTrue(errorVm.fieldErrors().contains("field2 must be positive"));
    }

    @Test
    void testErrorVmNotFound_Success() {
        ErrorVm errorVm = new ErrorVm("404", "Not Found", "Resource not found");

        assertNotNull(errorVm);
        assertEquals("404", errorVm.statusCode());
        assertEquals("Not Found", errorVm.title());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void testErrorVmInternalServerError_Success() {
        ErrorVm errorVm = new ErrorVm("500", "Internal Server Error", "An unexpected error occurred");

        assertNotNull(errorVm);
        assertEquals("500", errorVm.statusCode());
        assertEquals("Internal Server Error", errorVm.title());
    }

    @Test
    void testErrorVmWithMultipleFieldErrors_Success() {
        List<String> errors = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            errors.add("error" + i);
        }

        ErrorVm errorVm = new ErrorVm("422", "Unprocessable Entity", "Multiple validation errors", errors);

        assertEquals(5, errorVm.fieldErrors().size());
    }

    @Test
    void testErrorVmEmptyFieldErrors_Success() {
        List<String> emptyErrors = new ArrayList<>();
        ErrorVm errorVm = new ErrorVm("200", "OK", "Success", emptyErrors);

        assertNotNull(errorVm);
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void testErrorVmFieldErrorsImmutability_Success() {
        List<String> errors = new ArrayList<>();
        errors.add("error1");

        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Error occurred", errors);

        // Verify we can access the errors
        assertEquals(1, errorVm.fieldErrors().size());
    }
}
