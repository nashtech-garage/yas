package com.yas.observability.configuration;

import java.io.IOException;
import java.util.List;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

/**
 * Allows {@code @PropertySource} to load YAML files, which Spring does not support natively.
 * Used by {@link YasObservabilityAutoConfiguration} to contribute starter defaults at the
 * lowest possible precedence (application config, env vars, and system props all win).
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        List<PropertySource<?>> sources = new YamlPropertySourceLoader()
            .load(resource.getResource().getFilename(), resource.getResource());
        if (sources.isEmpty()) {
            throw new IllegalStateException("No property sources found in " + resource.getResource());
        }
        return sources.getFirst();
    }
}