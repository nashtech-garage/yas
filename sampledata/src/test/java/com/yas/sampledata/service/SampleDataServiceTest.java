package com.yas.sampledata.service;

import com.yas.sampledata.viewmodel.SampleDataVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SampleDataServiceTest {

    private DataSource productDataSource;
    private DataSource mediaDataSource;
    private SampleDataService sampleDataService;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        productDataSource = mock(DataSource.class);
        mediaDataSource = mock(DataSource.class);
        connection = mock(Connection.class);

        when(productDataSource.getConnection()).thenReturn(connection);
        when(mediaDataSource.getConnection()).thenReturn(connection);

        sampleDataService = new SampleDataService(productDataSource, mediaDataSource);
    }

    @Test
    void testCreateSampleData() throws SQLException {
        SampleDataVm result = sampleDataService.createSampleData();

        assertEquals("Insert Sample Data successfully!", result.message());
        verify(productDataSource, times(1)).getConnection();
        verify(mediaDataSource, times(1)).getConnection();
    }
}
