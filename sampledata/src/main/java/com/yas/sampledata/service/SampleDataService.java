package com.yas.sampledata.service;

import com.yas.sampledata.utils.SpringScriptUtility;
import com.yas.sampledata.viewmodel.SampleDataVm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.*;

@Service
@Transactional
public class SampleDataService {
    @Value("${datasource.url}")
    protected String JDBC_URL;

    @Value("${datasource.username}")
    protected String USERNAME;

    @Value("${datasource.password}")
    protected String PASSWORD;

    @Value("${datasource.largeobject.min}")
    protected Integer PG_OID_MIN;

    @Value("${datasource.largeobject.max}")
    protected Integer PG_OID_MAX;

    private static final String MEDIA = "media";
    private static final String PRODUCT = "product";
    private static final String PG_QUERY = " SELECT COUNT(1) FROM pg_largeobject_metadata WHERE oid between ? and ? ";

    private Connection connection = null;

    public void addSample(String schema) throws SQLException {
        try {
            connection = DriverManager.getConnection(JDBC_URL + schema, USERNAME, PASSWORD);
            SpringScriptUtility.runScript(new File(ClassLoader.getSystemClassLoader()
                    .getResource("db/" + schema + ".sql").getFile()).toPath().toString(), connection);

            if (MEDIA.equals(schema)) {
                /** check image data exist in pg_largeobject_metadata or not **/
                PreparedStatement statement = connection.prepareStatement(PG_QUERY);
                statement.setInt(1, PG_OID_MIN);
                statement.setInt(2, PG_OID_MAX);
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
            assert connection != null;
            connection.close();
        }
    }

    public SampleDataVm addSampleData() throws SQLException {
        addSample(MEDIA);
        addSample(PRODUCT);
        return new SampleDataVm("Insert Sample Data successfully!");
    }
}
