package com.yas.sampledata.config;

import org.junit.jupiter.api.Test;
import javax.sql.DataSource;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DataSourceConfigTest {

    @Test
    void testDataSourceBeans() {
        DataSourceConfig config = new DataSourceConfig();
        config.driverClassName = "org.h2.Driver";
        config.productUrl = "jdbc:h2:mem:test1";
        config.mediaUrl = "jdbc:h2:mem:test2";
        config.username = "sa";
        config.password = "";

        assertNotNull(config.productDataSource());
        assertNotNull(config.mediaDataSource());
        assertNotNull(config.jdbcProduct(config.productDataSource()));
        assertNotNull(config.jdbcMedia(config.mediaDataSource()));
    }
}
