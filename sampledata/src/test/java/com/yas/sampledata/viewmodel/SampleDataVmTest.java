package com.yas.sampledata.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SampleDataVmTest {

    @Test
    void testSampleDataVm() {
        SampleDataVm sampleDataVm = new SampleDataVm("Test message");
        assertEquals("Test message", sampleDataVm.message());
    }
}
