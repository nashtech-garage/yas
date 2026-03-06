package com.yas.media.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void testErrorVmWithThreeParameters() {
        String statusCode = "400";
        String title = "Bad Request";
        String detail = "Invalid input";

        ErrorVm errorVm = new ErrorVm(statusCode, title, detail);

        assertEquals(statusCode, errorVm.statusCode());
        assertEquals(title, errorVm.title());
        assertEquals(detail, errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void testErrorVmWithFourParameters() {
        String statusCode = "422";
        String title = "Unprocessable Entity";
        String detail = "Validation failed";
        List<String> fieldErrors = Arrays.asList("field1: error", "field2: error");

        ErrorVm errorVm = new ErrorVm(statusCode, title, detail, fieldErrors);

        assertEquals(statusCode, errorVm.statusCode());
        assertEquals(title, errorVm.title());
        assertEquals(detail, errorVm.detail());
        assertEquals(fieldErrors, errorVm.fieldErrors());
        assertEquals(2, errorVm.fieldErrors().size());
    }

    @Test
    void testErrorVmWithEmptyFieldErrors() {
        String statusCode = "404";
        String title = "Not Found";
        String detail = "Resource not found";
        List<String> fieldErrors = new ArrayList<>();

        ErrorVm errorVm = new ErrorVm(statusCode, title, detail, fieldErrors);

        assertEquals(statusCode, errorVm.statusCode());
        assertEquals(title, errorVm.title());
        assertEquals(detail, errorVm.detail());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void testErrorVmWithMultipleFieldErrors() {
        List<String> fieldErrors = Arrays.asList(
            "username: must not be blank",
            "email: must be a valid email",
            "password: must be at least 8 characters"
        );

        ErrorVm errorVm = new ErrorVm("400", "Validation Error", "Invalid data", fieldErrors);

        assertEquals(3, errorVm.fieldErrors().size());
        assertTrue(errorVm.fieldErrors().contains("username: must not be blank"));
        assertTrue(errorVm.fieldErrors().contains("email: must be a valid email"));
    }

    @Test
    void testErrorVmWithNullDetail() {
        ErrorVm errorVm = new ErrorVm("500", "Server Error", null);

        assertEquals("500", errorVm.statusCode());
        assertEquals("Server Error", errorVm.title());
        assertEquals(null, errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
    }

    @Test
    void testErrorVmWithNullTitle() {
        ErrorVm errorVm = new ErrorVm("400", null, "Error details");

        assertEquals("400", errorVm.statusCode());
        assertEquals(null, errorVm.title());
        assertEquals("Error details", errorVm.detail());
    }

    @Test
    void testErrorVmDifferentStatusCodes() {
        ErrorVm badRequest = new ErrorVm("400", "Bad Request", "Invalid request");
        ErrorVm unauthorized = new ErrorVm("401", "Unauthorized", "Not authenticated");
        ErrorVm forbidden = new ErrorVm("403", "Forbidden", "No access");
        ErrorVm notFound = new ErrorVm("404", "Not Found", "Resource missing");
        ErrorVm serverError = new ErrorVm("500", "Internal Server Error", "Server error");

        assertEquals("400", badRequest.statusCode());
        assertEquals("401", unauthorized.statusCode());
        assertEquals("403", forbidden.statusCode());
        assertEquals("404", notFound.statusCode());
        assertEquals("500", serverError.statusCode());
    }

    @Test
    void testErrorVmRefConstructorCreatesEmptyList() {
        ErrorVm errorVm = new ErrorVm("400", "Error", "Details");

        List<String> fieldErrors = errorVm.fieldErrors();
        assertNotNull(fieldErrors);
        assertTrue(fieldErrors.isEmpty());
    }

    @Test
    void testErrorVmEquality() {
        List<String> errors = Arrays.asList("error1", "error2");
        ErrorVm vm1 = new ErrorVm("400", "Bad Request", "Invalid", errors);
        ErrorVm vm2 = new ErrorVm("400", "Bad Request", "Invalid", errors);

        assertNotNull(vm1);
        assertNotNull(vm2);
    }
}
