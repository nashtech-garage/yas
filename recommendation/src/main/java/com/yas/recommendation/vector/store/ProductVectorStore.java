package com.yas.recommendation.vector.store;

import com.yas.recommendation.vector.document.ProductDocument;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
public class ProductVectorStore extends AppVectorStore<ProductDocument> {

    public ProductVectorStore(VectorStore vectorStore) {
        super(ProductDocument.class, vectorStore);
    }
}
