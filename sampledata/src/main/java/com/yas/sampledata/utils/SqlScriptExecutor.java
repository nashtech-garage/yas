package com.yas.sampledata.utils;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

@Component
public class SqlScriptExecutor {

    public void executeScriptsForSchema(DataSource dataSource, String schema, String locationPattern) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(locationPattern);

            for (Resource resource : resources) {
                executeSqlScript(dataSource, schema, resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeSqlScript(DataSource dataSource, String schema, Resource resource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setSchema(schema); // Set the schema
            ScriptUtils.executeSqlScript(connection, resource);
            System.out.println("Executed script: " + resource.getFilename() + " on schema: " + schema);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}