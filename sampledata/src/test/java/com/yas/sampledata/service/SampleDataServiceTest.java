package com.yas.sampledata.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.yas.sampledata.utils.SqlScriptExecutor;
import com.yas.sampledata.viewmodel.SampleDataVm;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SampleDataServiceTest {

    @Mock
    private DataSource productDataSource;

    @Mock
    private DataSource mediaDataSource;

    private SampleDataService sampleDataService;

    @BeforeEach
    void setUp() {
        sampleDataService = new SampleDataService(productDataSource, mediaDataSource);
    }

    @Test
    void createSampleData_shouldReturnSuccessMessage() {
        try (MockedConstruction<SqlScriptExecutor> mocked = Mockito.mockConstruction(
            SqlScriptExecutor.class,
            (mock, context) -> doNothing().when(mock).executeScriptsForSchema(any(), any(), any())
        )) {
            SampleDataVm result = sampleDataService.createSampleData();

            assertNotNull(result);
            assertEquals("Insert Sample Data successfully!", result.message());
        }
    }
}
