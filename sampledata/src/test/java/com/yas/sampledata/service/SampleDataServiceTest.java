package com.yas.sampledata.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.yas.sampledata.viewmodel.SampleDataVm;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SampleDataServiceTest {

    private SampleDataService sampleDataService;
    private DataSource productDataSource;
    private DataSource mediaDataSource;

    @BeforeEach
    void setUp() {
        productDataSource = mock(DataSource.class);
        mediaDataSource = mock(DataSource.class);
        sampleDataService = new SampleDataService(productDataSource, mediaDataSource);
    }

    @Test
    void createSampleData_shouldReturnSuccessMessage() {
        SampleDataVm result = sampleDataService.createSampleData();
        assertEquals("Insert Sample Data successfully!", result.message());
    }
}
