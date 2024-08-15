package com.yas.sampledata.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ConfigurationPropertiesScan
public class DataSourceConfig {

    @Value("${spring.datasource.product.url}")
    protected String productUrl;
    @Value("${spring.datasource.media.url}")
    protected String mediaUrl;

    @Value("${spring.datasource.username}")
    protected String username;
    @Value("${spring.datasource.password}")
    protected String password;
    @Value("${spring.datasource.driver-class-name}")
    protected String driverClassName;

    @Bean(name = "productDataSource")
    @Primary
    public DataSource productDataSource() {
        return DataSourceBuilder.create()
            .driverClassName(driverClassName)
            .url(productUrl)
            .username(username)
            .password(password)
            .build();
    }

    @Bean(name = "mediaDataSource")
    public DataSource mediaDataSource() {
        return DataSourceBuilder.create()
            .driverClassName(driverClassName)
            .url(mediaUrl)
            .username(username)
            .password(password)
            .build();
    }

    @Primary
    @Bean(name = "jdbcProduct")
    public JdbcTemplate jdbcProduct(@Qualifier("productDataSource") DataSource productDataSource) {
        return new JdbcTemplate(productDataSource);
    }

    @Bean(name = "jdbcMedia")
    public JdbcTemplate jdbcMedia(@Qualifier("mediaDataSource") DataSource mediaDataSource) {
        return new JdbcTemplate(mediaDataSource);
    }

}