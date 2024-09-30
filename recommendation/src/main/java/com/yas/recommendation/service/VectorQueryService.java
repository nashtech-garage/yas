package com.yas.recommendation.service;

import java.util.List;

public interface VectorQueryService<V, K> {

    List<V> similaritySearch(String query);

}
