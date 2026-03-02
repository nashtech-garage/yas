package com.yas.commonlibrary.viewmodel.error;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void testErrorVmWithAllParameters() {
        // Given
        String statusCode = "400";
        String title = "Bad Request";
        String detail = "Invalid input";
        List<String> fieldErrors = Arrays.asList("field1: error1", "field2: error2");

        // When
        ErrorVm errorVm = new ErrorVm(statusCode, title, detail, fieldErrors);

        // Then
        assertEquals(statusCode, errorVm.statusCode());
        assertEquals(title, errorVm.title());
        assertEquals(detail, errorVm.detail());
        assertEquals(2, errorVm.fieldErrors().size());
        assertEquals("field1: error1", errorVm.fieldErrors().get(0));
    }

    @Test
    void testErrorVmWithThreeParameters() {
        // Given
        String statusCode = "404";
        String title = "Not Found";
        String detail = "Resource not found";

        // When
        ErrorVm errorVm = new ErrorVm(statusCode, title, detail);

        // Then
        assertEquals(statusCode, errorVm.statusCode());
        assertEquals(title, errorVm.title());
        assertEquals(detail, errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void testErrorVmWithEmptyFieldErrors() {
        // Given
        String statusCode = "500";
        String title = "Internal Server Error";
        String detail = "Something went wrong";
        List<String> fieldErrors = List.of();

        // When
        ErrorVm errorVm = new ErrorVm(statusCode, title, detail, fieldErrors);

        // Then
        assertEquals(statusCode, errorVm.statusCode());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void testErrorVmEquality() {
        // Given
        ErrorVm errorVm1 = new ErrorVm("400", "Bad Request", "Invalid");
        ErrorVm errorVm2 = new ErrorVm("400", "Bad Request", "Invalid");

        // Then
        assertEquals(errorVm1, errorVm2);
        assertEquals(errorVm1.hashCode(), errorVm2.hashCode());
    }
}
