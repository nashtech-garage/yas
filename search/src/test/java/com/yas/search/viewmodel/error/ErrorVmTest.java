package com.yas.search.viewmodel.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void testConstructor_whenAllFieldsProvided_thenAllFieldsSet() {
        // Given
        String statusCode = "404";
        String title = "Not Found";
        String detail = "Product not found";
        List<String> fieldErrors = List.of("Field1 error", "Field2 error");

        // When
        ErrorVm errorVm = new ErrorVm(statusCode, title, detail, fieldErrors);

        // Then
        assertNotNull(errorVm);
        assertEquals("404", errorVm.statusCode());
        assertEquals("Not Found", errorVm.title());
        assertEquals("Product not found", errorVm.detail());
        assertEquals(2, errorVm.fieldErrors().size());
        assertEquals("Field1 error", errorVm.fieldErrors().get(0));
        assertEquals("Field2 error", errorVm.fieldErrors().get(1));
    }

    @Test
    void testConstructor_whenFieldErrorsNotProvided_thenEmptyListCreated() {
        // Given
        String statusCode = "400";
        String title = "Bad Request";
        String detail = "Invalid input";

        // When
        ErrorVm errorVm = new ErrorVm(statusCode, title, detail);

        // Then
        assertNotNull(errorVm);
        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad Request", errorVm.title());
        assertEquals("Invalid input", errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void testConstructor_whenEmptyFieldErrorsList_thenEmptyListSet() {
        // Given
        String statusCode = "500";
        String title = "Internal Server Error";
        String detail = "Server error occurred";
        List<String> fieldErrors = new ArrayList<>();

        // When
        ErrorVm errorVm = new ErrorVm(statusCode, title, detail, fieldErrors);

        // Then
        assertNotNull(errorVm);
        assertEquals("500", errorVm.statusCode());
        assertEquals("Internal Server Error", errorVm.title());
        assertEquals("Server error occurred", errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void testConstructor_whenNullValues_thenNullValuesSet() {
        // Given
        String statusCode = null;
        String title = null;
        String detail = null;

        // When
        ErrorVm errorVm = new ErrorVm(statusCode, title, detail);

        // Then
        assertNotNull(errorVm);
        assertEquals(null, errorVm.statusCode());
        assertEquals(null, errorVm.title());
        assertEquals(null, errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }
}
