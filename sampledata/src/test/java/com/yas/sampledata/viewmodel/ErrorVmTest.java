package com.yas.sampledata.viewmodel;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorVmTest {

    @Test
    void testErrorVmConstructorWithFieldErrors() {
        List<String> fieldErrors = List.of("Field cannot be null");
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Validation failed", fieldErrors);

        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad Request", errorVm.title());
        assertEquals("Validation failed", errorVm.detail());
        assertEquals(fieldErrors, errorVm.fieldErrors());
    }

    @Test
    void testErrorVmConstructorWithoutFieldErrors() {
        ErrorVm errorVm = new ErrorVm("500", "Internal Server Error", "Something went wrong");

        assertEquals("500", errorVm.statusCode());
        assertEquals("Internal Server Error", errorVm.title());
        assertEquals("Something went wrong", errorVm.detail());
        assertEquals(0, errorVm.fieldErrors().size());
    }
}
