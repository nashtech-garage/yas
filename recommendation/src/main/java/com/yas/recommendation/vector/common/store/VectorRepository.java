package com.yas.recommendation.vector.common.store;

import com.yas.recommendation.vector.common.document.BaseDocument;

public interface VectorRepository<D extends BaseDocument, E> {

    E getEntity(Long entityId);

    void add(Long entityId);

    void delete(Long entityId);

    void update(Long entityId);
}
