package com.yas.sampledata.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SqlScriptExecutorTest {

    private DataSource dataSource;
    private Connection connection;
    private SqlScriptExecutor sqlScriptExecutor;

    @BeforeEach
    void setUp() throws SQLException {
        dataSource = Mockito.mock(DataSource.class);
        connection = Mockito.mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        sqlScriptExecutor = new SqlScriptExecutor();
    }

    @Test
    void executeScriptsForSchema_withNoResources_shouldNotFail() {
        // When
        sqlScriptExecutor.executeScriptsForSchema(dataSource, "public", "non-existent-path/*.sql");

        // Then
        // Should not throw exception
    }

    @Test
    void executeScriptsForSchema_withResources_shouldExecuteSuccessfully() throws SQLException {
        // When
        sqlScriptExecutor.executeScriptsForSchema(dataSource, "public", "classpath:db/test.sql");

        // Then
        verify(dataSource, atLeastOnce()).getConnection();
        verify(connection, atLeastOnce()).setSchema("public");
    }

    @Test
    void executeScriptsForSchema_withSQLException_shouldHandleGracefully() throws SQLException {
        // Given
        when(dataSource.getConnection()).thenThrow(new SQLException("Test exception"));

        // When
        sqlScriptExecutor.executeScriptsForSchema(dataSource, "public", "classpath:db/test.sql");

        // Then
        // Handled gracefully
    }
}
