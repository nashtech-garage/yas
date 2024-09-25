package com.yas.search.config;

import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class ElasticTestContainer extends ElasticsearchContainer {

  private static final String DOCKER_ELASTIC = "docker.elastic.co/elasticsearch/elasticsearch:7.17.6";

  private static final String CLUSTER_NAME = "sample-cluster";

  private static final String ELASTIC_SEARCH = "elasticsearch";

  public ElasticTestContainer() {
    super(DOCKER_ELASTIC);
    this.addFixedExposedPort(9200, 9200);
    this.addFixedExposedPort(9300, 9300);
    this.addEnv(CLUSTER_NAME, ELASTIC_SEARCH);
  }
}
