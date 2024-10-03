package com.yas.recommendation.startup;

import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultDataLoader implements ApplicationRunner {

    private final DataLoaderService dataLoaderService;
    private final EmbeddingSearchConfiguration embeddingSearchConfiguration;

    public DefaultDataLoader(
        DataLoaderService dataLoaderService,
        EmbeddingSearchConfiguration embeddingSearchConfiguration
    ) {
        this.dataLoaderService = dataLoaderService;
        this.embeddingSearchConfiguration = embeddingSearchConfiguration;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (embeddingSearchConfiguration.initDefaultData()) {
            dataLoaderService.load();
        }
    }
}
