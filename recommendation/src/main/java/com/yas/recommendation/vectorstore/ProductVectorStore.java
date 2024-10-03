package com.yas.recommendation.vectorstore;

import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
public class ProductVectorStore<D extends Document> implements AppVectorStore<D>  {

    private final VectorStore vectorStore;

    public ProductVectorStore(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void add(List<D> docs) {
        vectorStore.add(new ArrayList<>(docs));
    }

    @Override
    public void delete(List<Long> ids) {
        // call repository here
    }
}
