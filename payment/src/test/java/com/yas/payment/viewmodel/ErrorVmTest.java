package com.yas.payment.viewmodel;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorVmTest {

    @Test
    void testErrorVmWith4Parameters() {
        // Given
        String statusCode = "400";
        String title = "Bad Request";
        String detail = "Invalid input";
        List<String> fieldErrors = Arrays.asList("field1", "field2");

        // When
        ErrorVm errorVm = new ErrorVm(statusCode, title, detail, fieldErrors);

        // Then
        assertNotNull(errorVm);
        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad Request", errorVm.title());
        assertEquals("Invalid input", errorVm.detail());
        assertEquals(2, errorVm.fieldErrors().size());
        assertTrue(errorVm.fieldErrors().contains("field1"));
        assertTrue(errorVm.fieldErrors().contains("field2"));
    }

    @Test
    void testErrorVmWith3Parameters() {
        // Given
        String statusCode = "404";
        String title = "Not Found";
        String detail = "Resource not found";

        // When
        ErrorVm errorVm = new ErrorVm(statusCode, title, detail);

        // Then
        assertNotNull(errorVm);
        assertEquals("404", errorVm.statusCode());
        assertEquals("Not Found", errorVm.title());
        assertEquals("Resource not found", errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void testErrorVmEquality() {
        // Given
        ErrorVm error1 = new ErrorVm("500", "Server Error", "Internal error");
        ErrorVm error2 = new ErrorVm("500", "Server Error", "Internal error");

        // Then
        assertEquals(error1, error2);
        assertEquals(error1.hashCode(), error2.hashCode());
    }

    @Test
    void testErrorVmToString() {
        // Given
        ErrorVm errorVm = new ErrorVm("403", "Forbidden", "Access denied");

        // When & Then
        assertNotNull(errorVm.toString());
        assertTrue(errorVm.toString().contains("403"));
    }
}
