package com.yas.commonlibrary.viewmodel.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void constructor_WithThreeArgs_InitializesEmptyFieldErrors() {
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Detail");
        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad Request", errorVm.title());
        assertEquals("Detail", errorVm.detail());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void record_Accessors_WorkCorrectly() {
        ErrorVm errorVm = new ErrorVm("500", "Error", "Detailed error", java.util.List.of("error1"));
        assertEquals("500", errorVm.statusCode());
        assertEquals("Error", errorVm.title());
        assertEquals("Detailed error", errorVm.detail());
        assertEquals(1, errorVm.fieldErrors().size());
        assertEquals("error1", errorVm.fieldErrors().get(0));
    }
}
