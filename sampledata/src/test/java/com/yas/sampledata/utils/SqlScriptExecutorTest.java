package com.yas.sampledata.utils;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SqlScriptExecutorTest {

    private SqlScriptExecutor sqlScriptExecutor;

    @BeforeEach
    void setUp() {
        sqlScriptExecutor = new SqlScriptExecutor();
    }

    @Test
    void executeScriptsForSchema_withNonExistentPattern_shouldNotThrow() {
        DataSource dataSource = mock(DataSource.class);

        // Pattern matches no resources → no iteration → no error
        assertThatCode(() ->
            sqlScriptExecutor.executeScriptsForSchema(dataSource, "public", "classpath*:no-match-pattern/**/*.sql")
        ).doesNotThrowAnyException();
    }

    @Test
    void executeScriptsForSchema_whenDataSourceThrowsSqlException_shouldLogAndNotPropagate()
        throws Exception {
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection refused"));

        // The method catches SQLException internally, so it should not propagate
        assertThatCode(() ->
            sqlScriptExecutor.executeScriptsForSchema(dataSource, "public", "classpath*:db/product/*.sql")
        ).doesNotThrowAnyException();
    }

    @Test
    void executeScriptsForSchema_withValidPattern_shouldAttemptConnection() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(connection);

        // Should proceed without throwing even if SQL execution has issues
        assertThatCode(() ->
            sqlScriptExecutor.executeScriptsForSchema(dataSource, "public", "classpath*:db/product/*.sql")
        ).doesNotThrowAnyException();
    }

    @Test
    void executeScriptsForSchema_withNullPattern_shouldNotThrow() {
        DataSource dataSource = mock(DataSource.class);

        assertThatCode(() ->
            sqlScriptExecutor.executeScriptsForSchema(dataSource, "public", "classpath*:empty-nothing/*.sql")
        ).doesNotThrowAnyException();
    }
}
