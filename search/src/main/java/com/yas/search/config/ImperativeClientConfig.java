package com.yas.search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.yas.elasticsearch.repository")
@ComponentScan(basePackages = {"com.yas.elasticsearch.service"})
public class ImperativeClientConfig extends ElasticsearchConfiguration {
    @Value("${elasticsearch.url}")
    private String ELASTICSEARCH_URL;

    @Override
    public ClientConfiguration clientConfiguration() {
        return  ClientConfiguration.builder()
                .connectedTo(ELASTICSEARCH_URL)
                .build();
    }
}
