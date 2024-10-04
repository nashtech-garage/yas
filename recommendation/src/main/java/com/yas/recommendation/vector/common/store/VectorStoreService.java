package com.yas.recommendation.vector.common.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.recommendation.vector.common.document.BaseDocument;
import com.yas.recommendation.vector.common.document.DefaultIdGenerator;
import com.yas.recommendation.vector.common.document.DocumentMetadata;
import com.yas.recommendation.vector.common.formatter.DocumentFormatter;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.ai.document.id.IdGenerator;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Abstract class for managing vector store operations for documents.
 *
 * @param <D> the type of document
 */
@Component
public abstract class VectorStoreService<D extends BaseDocument, E> implements VectorRepository<D, E> {

    private ObjectMapper objectMapper;

    private final Class<D> docType;
    private final VectorStore vectorStore;
    private final DocumentMetadata documentMetadata;
    private final DocumentFormatter documentFormatter;

    @SneakyThrows
    public VectorStoreService(Class<D> docType, VectorStore vectorStore) {
        Assert.isTrue(
            docType.isAnnotationPresent(DocumentMetadata.class),
            "Document must be annotated by '@DocumentFormat'"
        );
        this.docType = docType;
        this.vectorStore = vectorStore;
        this.documentMetadata = docType.getAnnotation(DocumentMetadata.class);
        this.documentFormatter = documentMetadata.documentFormatter().getDeclaredConstructor().newInstance();
    }

    /**
     * Retrieves the entity data for a given product ID.
     *
     * @param id the ID.
     * @return a map of entity attributes where keys are attribute names and values are their corresponding values.
     */
    public abstract E getEntity(Long id);

    @SneakyThrows
    public void add(Long entityId) {
        final var entity = getEntity(entityId);
        final var entityContentMap = objectMapper.convertValue(entity, Map.class);
        D document = docType.getDeclaredConstructor().newInstance();
        document.setContent(documentFormatter.format(entityContentMap, documentMetadata.contentFormat()));
        document.setMetadata(entityContentMap);

        final IdGenerator idGenerator = getIdGenerator(entityId);
        vectorStore.add(List.of(document.toDocument(idGenerator)));
    }

    public void delete(Long entityId) {
        DefaultIdGenerator defaultIdGenerator = new DefaultIdGenerator(documentMetadata.docIdPrefix(), entityId);
        var docId = defaultIdGenerator.generateId();
        vectorStore.delete(List.of(docId));
    }

    public void update(Long entityId) {
        delete(entityId);
        add(entityId);
    }

    protected IdGenerator getIdGenerator(Long entityId) {
        return new DefaultIdGenerator(documentMetadata.docIdPrefix(), entityId);
    }

    @Autowired
    private void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
