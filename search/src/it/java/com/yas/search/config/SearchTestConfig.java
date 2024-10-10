package com.yas.search.config;

import com.yas.commonlibrary.IntegrationTestConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SearchTestConfig extends IntegrationTestConfiguration {

    @Bean(destroyMethod = "stop")
    @ServiceConnection
    public ElasticTestContainer elasticTestContainer() {
        return new ElasticTestContainer();
    }
}
