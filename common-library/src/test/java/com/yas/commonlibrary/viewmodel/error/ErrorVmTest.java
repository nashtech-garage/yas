package com.yas.commonlibrary.viewmodel.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void constructorWithoutFieldErrors_InitializesEmptyList() {
        ErrorVm errorVm = new ErrorVm("400", "Bad request", "Validation failed");

        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad request", errorVm.title());
        assertEquals("Validation failed", errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void canonicalConstructor_PreservesProvidedFieldErrors() {
        List<String> fieldErrors = List.of("email is invalid");
        ErrorVm errorVm = new ErrorVm("400", "Bad request", "Validation failed", fieldErrors);

        assertEquals(fieldErrors, errorVm.fieldErrors());
    }
}
