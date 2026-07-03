package com.yas.customer.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorVmTest {

    @Test
    void testErrorVmConstructor() {
        ErrorVm errorVm = new ErrorVm("404", "Not Found", "Item not found");
        assertEquals("404", errorVm.statusCode());
        assertEquals("Not Found", errorVm.title());
        assertEquals("Item not found", errorVm.detail());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }
}
