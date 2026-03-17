package com.yas.search.config;

import com.yas.commonlibrary.IntegrationTestConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;

@TestConfiguration
public class SearchTestConfig extends IntegrationTestConfiguration {

    @Value("${elasticsearch.version}")
    private String elasticSearchVersion;

    @Bean(destroyMethod = "stop")
    @ServiceConnection
    public ElasticTestContainer elasticTestContainer() {
        return new ElasticTestContainer(elasticSearchVersion);
    }

    @Bean
    public DynamicPropertyRegistrar elasticProperties(ElasticTestContainer elastic) {
        return registry -> registry.add("elasticsearch.url", elastic::getHttpHostAddress);
    }
}
