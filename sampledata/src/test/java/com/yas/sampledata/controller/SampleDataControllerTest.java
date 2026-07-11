package com.yas.sampledata.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.sampledata.service.SampleDataService;
import com.yas.sampledata.viewmodel.SampleDataVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SampleDataControllerTest {

    @Mock
    private SampleDataService sampleDataService;

    @InjectMocks
    private SampleDataController sampleDataController;

    @Test
    void createSampleData_shouldDelegateToServiceAndReturnResult() {
        SampleDataVm expected = new SampleDataVm("Insert Sample Data successfully!");
        when(sampleDataService.createSampleData()).thenReturn(expected);

        SampleDataVm result = sampleDataController.createSampleData(new SampleDataVm("test"));

        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("Insert Sample Data successfully!");
        verify(sampleDataService, times(1)).createSampleData();
    }

    @Test
    void createSampleData_shouldCallServiceExactlyOnce() {
        SampleDataVm response = new SampleDataVm("done");
        when(sampleDataService.createSampleData()).thenReturn(response);

        sampleDataController.createSampleData(new SampleDataVm("any"));

        verify(sampleDataService, times(1)).createSampleData();
    }

    @Test
    void createSampleData_returnedVmShouldMatchServiceResult() {
        SampleDataVm serviceResult = new SampleDataVm("custom message");
        when(sampleDataService.createSampleData()).thenReturn(serviceResult);

        SampleDataVm result = sampleDataController.createSampleData(new SampleDataVm("input"));

        assertThat(result).isSameAs(serviceResult);
    }
}
