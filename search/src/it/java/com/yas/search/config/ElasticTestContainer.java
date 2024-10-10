package com.yas.search.config;

import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class ElasticTestContainer extends ElasticsearchContainer {

    private static final String IMAGE_NAME = "docker.elastic.co/elasticsearch/elasticsearch:%s";

    private static final String CLUSTER_NAME = "cluster.name";

    private static final String ELASTIC_SEARCH = "elasticsearch";

    public ElasticTestContainer(String version) {
        super(IMAGE_NAME.formatted(version));
        this.addFixedExposedPort(9200, 9200);
        this.addEnv(CLUSTER_NAME, ELASTIC_SEARCH);
        this.withEnv("xpack.security.transport.ssl.enabled", "false");
        this.withEnv("xpack.security.http.ssl.enabled", "false");
    }
}
