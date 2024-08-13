package com.yas.sampledata.service;

import com.yas.sampledata.utils.SpringScriptUtility;
import com.yas.sampledata.viewmodel.SampleDataVm;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SampleDataService {
    private static final String MEDIA = "media";
    private static final String PRODUCT = "product";
    private static final String PG_QUERY = " SELECT COUNT(1) FROM pg_largeobject_metadata WHERE oid between ? and ? ";
    @Value("${datasource.url}")
    protected String url;
    @Value("${datasource.username}")
    protected String username;
    @Value("${datasource.password}")
    protected String password;
    @Value("${datasource.largeobject.min}")
    protected Integer pgOidMin;
    @Value("${datasource.largeobject.max}")
    protected Integer pgOidMax;
    private Connection connection = null;

    public void addSample(String schema) throws SQLException {
        try {
            connection = DriverManager.getConnection(url + schema, username, password);
            SpringScriptUtility.runScript(new File(ClassLoader.getSystemClassLoader()
                .getResource("db/" + schema + ".sql").getFile()).toPath().toString(), connection);

            if (MEDIA.equals(schema)) {
                PreparedStatement statement = connection.prepareStatement(PG_QUERY);
                statement.setInt(1, pgOidMin);
                statement.setInt(2, pgOidMax);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    if (count == 0) {
                        SpringScriptUtility.runScript(new File(ClassLoader.getSystemClassLoader()
                            .getResource("db/pg_largeobject.sql").getFile()).toPath().toString(), connection);

                        SpringScriptUtility.runScript(new File(ClassLoader.getSystemClassLoader()
                            .getResource("db/pg_largeobject_metadata.sql").getFile()).toPath().toString(), connection);
                    }
                }
                resultSet.close();
                statement.close();
            }

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public SampleDataVm addSampleData() throws SQLException {
        addSample(MEDIA);
        addSample(PRODUCT);
        return new SampleDataVm("Insert Sample Data successfully!");
    }
}
