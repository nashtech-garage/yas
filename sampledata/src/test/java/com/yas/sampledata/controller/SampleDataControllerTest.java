package com.yas.sampledata.controller;

import com.yas.sampledata.service.SampleDataService;
import com.yas.sampledata.viewmodel.SampleDataVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SampleDataControllerTest {

    private SampleDataService sampleDataService;
    private SampleDataController sampleDataController;

    @BeforeEach
    void setUp() {
        sampleDataService = mock(SampleDataService.class);
        sampleDataController = new SampleDataController(sampleDataService);
    }

    @Test
    void testCreateSampleData() {
        SampleDataVm expectedVm = new SampleDataVm("Success");
        when(sampleDataService.createSampleData()).thenReturn(expectedVm);

        SampleDataVm actualVm = sampleDataController.createSampleData(new SampleDataVm("Request"));

        assertEquals(expectedVm, actualVm);
        verify(sampleDataService, times(1)).createSampleData();
    }
}
