package com.yas.recommendation.vectorstore;

import java.util.List;
import org.springframework.ai.document.Document;

public interface AppVectorStore<D extends Document> {
    void add(List<D> docs);

    void delete(List<Long> ids);
}
