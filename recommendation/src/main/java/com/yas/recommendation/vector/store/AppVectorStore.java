package com.yas.recommendation.vector.store;

import com.yas.recommendation.vector.document.DocumentFormat;
import com.yas.recommendation.vector.document.IDocument;
import com.yas.recommendation.vector.document.ProductDocument;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.document.ContentFormatter;
import org.springframework.ai.document.DefaultContentFormatter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.util.Assert;

public abstract class AppVectorStore<D extends Document & IDocument> {

    private final VectorStore vectorStore;
    private final ContentFormatter entityContentFormatter;

    public AppVectorStore(Class<D> docType, VectorStore vectorStore) {
        Assert.isTrue(
            docType.isAnnotationPresent(DocumentFormat.class),
            "Document must be annotated by '@DocumentFormat'"
        );
        this.vectorStore = vectorStore;
        this.entityContentFormatter = DefaultContentFormatter
            .builder()
            .from(DefaultContentFormatter.defaultConfig())
            .withTextTemplate(docType.getAnnotation(DocumentFormat.class).embeddingContentFormatter())
            .build();
    }

    public void add(List<ProductDocument> docs) {
        docs.forEach(doc -> doc.setContentFormatter(entityContentFormatter));
        vectorStore.add(new ArrayList<>(docs));
    }

    public void delete(List<Long> ids) {
        // call repository here
    }
}
