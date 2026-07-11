package com.yas.sampledata.service;

import com.yas.sampledata.utils.SqlScriptExecutor;
import com.yas.sampledata.viewmodel.SampleDataVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SampleDataServiceTest {

    private DataSource productDataSource;
    private DataSource mediaDataSource;
    private SqlScriptExecutor sqlScriptExecutor;
    private SampleDataService sampleDataService;

    @BeforeEach
    void setUp() {
        productDataSource = Mockito.mock(DataSource.class);
        mediaDataSource = Mockito.mock(DataSource.class);
        sqlScriptExecutor = Mockito.mock(SqlScriptExecutor.class);
        sampleDataService = new SampleDataService(productDataSource, mediaDataSource, sqlScriptExecutor);
    }

    @Test
    void createSampleData_shouldExecuteScriptsAndReturnSuccessMessage() {
        // When
        SampleDataVm result = sampleDataService.createSampleData();

        // Then
        assertEquals("Insert Sample Data successfully!", result.message());
        verify(sqlScriptExecutor, times(1)).executeScriptsForSchema(eq(productDataSource), eq("public"), anyString());
        verify(sqlScriptExecutor, times(1)).executeScriptsForSchema(eq(mediaDataSource), eq("public"), anyString());
    }
}
