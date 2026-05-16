package com.yas.search.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.yas.search.repository")
@ComponentScan(basePackages = "com.yas.search.service")
@RequiredArgsConstructor
public class ImperativeClientConfig extends ElasticsearchConfiguration {

    private final ElasticsearchDataConfig elasticsearchConfig;

    @Override
    public ClientConfiguration clientConfiguration() {
        String url = elasticsearchConfig.getUrl()
            .replace("https://", "")
            .replace("http://", "");
    
        return ClientConfiguration.builder()
                .connectedTo(url)
                .usingSsl()
                .withBasicAuth(elasticsearchConfig.getUsername(), 
                            elasticsearchConfig.getPassword())
                .build();
    }
}
