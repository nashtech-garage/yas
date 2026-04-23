package com.yas.recommendation.vector.common.store;

import com.yas.recommendation.vector.common.document.BaseDocument;
import java.util.List;

public interface VectorRepository<D extends BaseDocument, E> {

    List<D> search(Long id);

    E getEntity(Long entityId);

    void add(Long entityId);

    void delete(Long entityId);

    void update(Long entityId);

}
