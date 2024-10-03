package com.yas.recommendation.vector.store;

import com.yas.recommendation.vector.document.DocumentMetadata;
import com.yas.recommendation.vector.document.ProductDocument;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.document.ContentFormatter;
import org.springframework.ai.document.DefaultContentFormatter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.util.Assert;

/**
 * Abstract class for managing vector store operations for documents.
 *
 * @param <D> the type of document, which must extend {@link Document}.
 */
public abstract class AppVectorStore<D extends Document> {

    private final VectorStore vectorStore;
    private final ContentFormatter entityContentFormatter;

    public AppVectorStore(Class<D> docType, VectorStore vectorStore) {
        Assert.isTrue(
            docType.isAnnotationPresent(DocumentMetadata.class),
            "Document must be annotated by '@DocumentFormat'"
        );
        this.vectorStore = vectorStore;
        this.entityContentFormatter = DefaultContentFormatter
            .builder()
            .from(DefaultContentFormatter.defaultConfig())
            .withTextTemplate(docType.getAnnotation(DocumentMetadata.class).embeddingContentFormatter())
            .build();
    }

    /**
     * Adds a list of documents to the vector store.
     * Ensures that records are stored according to the configuration of {@link D}.
     *
     * @param docs the documents to add.
     */
    public void add(List<ProductDocument> docs) {
        // The ProductDocument itself uses its custom content formatter.
        // This line prevents issues if a document is created using Document.builder(),
        // as the configuration in ProductDocument would be overridden.
        docs.forEach(doc -> doc.setContentFormatter(entityContentFormatter));
        vectorStore.add(new ArrayList<>(docs));
    }

    public void delete(List<Long> ids) {
        // call repository here
    }
}
