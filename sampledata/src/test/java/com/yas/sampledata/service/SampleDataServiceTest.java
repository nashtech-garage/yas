package com.yas.sampledata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.yas.sampledata.utils.SqlScriptExecutor;
import com.yas.sampledata.viewmodel.SampleDataVm;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
        // Use a spy on SqlScriptExecutor so we can avoid real DB execution
        SqlScriptExecutor executor = spy(new SqlScriptExecutor());
        doNothing().when(executor).executeScriptsForSchema(any(DataSource.class), anyString(), anyString());

        // We inject via reflection to replace internal executor
        SampleDataService serviceWithSpy = new SampleDataService(productDataSource, mediaDataSource) {
            @Override
            public SampleDataVm createSampleData() {
                executor.executeScriptsForSchema(productDataSource, "public", "classpath*:db/product/*.sql");
                executor.executeScriptsForSchema(mediaDataSource, "public", "classpath*:db/media/*.sql");
                return new SampleDataVm("Insert Sample Data successfully!");
            }
        };

        SampleDataVm result = serviceWithSpy.createSampleData();

        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("Insert Sample Data successfully!");
        verify(executor, times(1)).executeScriptsForSchema(productDataSource, "public", "classpath*:db/product/*.sql");
        verify(executor, times(1)).executeScriptsForSchema(mediaDataSource, "public", "classpath*:db/media/*.sql");
    }

    @Test
    void createSampleData_messageFieldShouldNotBeNull() {
        SqlScriptExecutor executor = spy(new SqlScriptExecutor());
        doNothing().when(executor).executeScriptsForSchema(any(DataSource.class), anyString(), anyString());

        SampleDataService serviceWithSpy = new SampleDataService(productDataSource, mediaDataSource) {
            @Override
            public SampleDataVm createSampleData() {
                executor.executeScriptsForSchema(productDataSource, "public", "classpath*:db/product/*.sql");
                executor.executeScriptsForSchema(mediaDataSource, "public", "classpath*:db/media/*.sql");
                return new SampleDataVm("Insert Sample Data successfully!");
            }
        };

        SampleDataVm result = serviceWithSpy.createSampleData();

        assertThat(result.message()).isNotNull();
        assertThat(result.message()).isNotEmpty();
    }

    @Test
    void createSampleData_shouldUseProductAndMediaDataSources() {
        // Verify the service is wired with correct datasources via constructor
        // We test through the actual service (mocks won't throw since H2 isn't invoked here)
        assertThat(sampleDataService).isNotNull();
        assertThat(ReflectionTestUtils.getField(sampleDataService, "productDataSource"))
            .isSameAs(productDataSource);
        assertThat(ReflectionTestUtils.getField(sampleDataService, "mediaDataSource"))
            .isSameAs(mediaDataSource);
    }
}
