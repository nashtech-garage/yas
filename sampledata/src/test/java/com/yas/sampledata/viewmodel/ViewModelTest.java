package com.yas.sampledata.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ViewModelTest {

    @Test
    void testSampleDataVm() {
        SampleDataVm vm1 = new SampleDataVm("test");
        SampleDataVm vm2 = new SampleDataVm("test");
        assertEquals("test", vm1.message());
        assertEquals(vm1, vm2);
        assertEquals(vm1.hashCode(), vm2.hashCode());
        assertNotNull(vm1.toString());
    }

    @Test
    void testErrorVm() {
        ErrorVm vm1 = new ErrorVm("400", "Bad Request", "Detail");
        ErrorVm vm2 = new ErrorVm("400", "Bad Request", "Detail", java.util.Collections.emptyList());
        assertEquals("400", vm1.statusCode());
        assertEquals("Bad Request", vm1.title());
        assertEquals("Detail", vm1.detail());
        assertTrue(vm1.fieldErrors().isEmpty());
        assertEquals(vm1.statusCode(), vm2.statusCode());
        assertNotNull(vm1.toString());
    }
}
