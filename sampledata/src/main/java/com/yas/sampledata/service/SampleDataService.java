package com.yas.sampledata.service;

import com.yas.sampledata.utils.SpringScriptUtility;
import com.yas.sampledata.viewmodel.SampleDataVm;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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

    private InputStream getFileFromResourceAsStream(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    private File convertToFile(String path) throws IOException {
        InputStream inputStream = getFileFromResourceAsStream(path);
        File tempFile = File.createTempFile("temp", ".sql", new File("/sqlTempDir"));
        try {
            FileUtils.copyInputStreamToFile(inputStream, tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public void addSample(String schema) throws SQLException {
        try {
            connection = DriverManager.getConnection(url + schema, username, password);
            SpringScriptUtility.runScript(convertToFile(("db/" + schema + ".sql")).toPath().toString(), connection);

            if (MEDIA.equals(schema)) {
                PreparedStatement statement = connection.prepareStatement(PG_QUERY);
                statement.setInt(1, pgOidMin);
                statement.setInt(2, pgOidMax);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    if (count == 0) {
                        SpringScriptUtility.runScript(
                                convertToFile(("db/pg_largeobject.sql")).toPath().toString(), connection);
                        SpringScriptUtility.runScript(
                                convertToFile(("db/pg_largeobject_metadata.sql")).toPath().toString(), connection);
                    }
                }
                resultSet.close();
                statement.close();
            }

        } catch (SQLException | IOException e) {
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
