package com.yas.recommendation.vector.common.store;

import tools.jackson.databind.ObjectMapper;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.vector.common.document.BaseDocument;
import com.yas.recommendation.vector.common.document.DefaultIdGenerator;
import com.yas.recommendation.vector.common.document.DocumentMetadata;
import com.yas.recommendation.vector.common.formatter.DocumentFormatter;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.id.IdGenerator;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Abstract class for managing vector store operations for documents.
 *
 * @param <D> type of document will be store
 * @param <E> type of fetch document
 */
@Component
public abstract class SimpleVectorRepository<D extends BaseDocument, E> implements VectorRepository<D, E> {

    public static final String FIELD_ID = "id";
    public static final String TYPE_METADATA = "type";

    private ObjectMapper objectMapper;
    private EmbeddingSearchConfiguration embeddingSearchConfiguration;

    private final Class<D> docType;
    private final VectorStore vectorStore;
    private final DocumentMetadata documentMetadata;
    private final DocumentFormatter documentFormatter;

    /**
     * Constructor for SimpleVectorRepository.
     *
     * @param docType     type of document will be store.
     * @param vectorStore vector store service.
     */
    @SneakyThrows
    protected SimpleVectorRepository(Class<D> docType, VectorStore vectorStore) {
        Assert.isTrue(docType.isAnnotationPresent(DocumentMetadata.class),
                "Document must be annotated by '@DocumentFormat'");
        this.docType = docType;
        this.vectorStore = vectorStore;
        this.documentMetadata = docType.getAnnotation(DocumentMetadata.class);
        this.documentFormatter = documentMetadata.documentFormatter().getDeclaredConstructor().newInstance();
    }

    /**
     * Add a record to the vector database by fetching data from an external source.
     * This method retrieves the entity using
     * {@link com.yas.recommendation.vector.common.store.SimpleVectorRepository#getEntity(Long)}
     * and then formats the fetched document before saving it to the database.
     *
     * @param entityId the ID of the entity to fetch and add to the vector database
     */
    @lombok.SneakyThrows
    public void add(Long entityId) {
        final var entity = getEntity(entityId);
        final var entityContentMap = objectMapper.convertValue(entity, Map.class);

        D document = docType.getDeclaredConstructor().newInstance();
        document.setContent(documentFormatter.format(entityContentMap, documentMetadata.contentFormat(), objectMapper));

        entityContentMap.put(TYPE_METADATA, documentMetadata.docIdPrefix());
        document.setMetadata(entityContentMap);

        final IdGenerator idGenerator = getIdGenerator(entityId);
        vectorStore.add(List.of(document.toDocument(idGenerator)));
    }

    /**
     * Deletes a record from the vector store based on the provided entity ID.
     *
     * @param entityId the ID of the entity to be deleted from the vector store
     */
    public void delete(Long entityId) {
        IdGenerator idGenerator = getIdGenerator(entityId);
        var docId = idGenerator.generateId();
        vectorStore.delete(List.of(docId));
    }

    /**
     * Updates a record in the vector store for the given entity ID.
     * The method first deletes the existing record by invoking {@link #delete(Long)}
     * and then adds a new record using {@link #add(Long)}.
     *
     * @param entityId the ID of the entity to be updated in the vector store
     */
    public void update(Long entityId) {
        delete(entityId);
        add(entityId);
    }

    /**
     * Performs a similarity search based on a product ID.
     *
     * @param id the ID of the product for which to perform the similarity search.
     * @return a list of product results that are similar to the specified product.
     */
    @Override
    public List<D> search(Long id) {
        final var entity = getEntity(id);
        final var entityContentMap = objectMapper.convertValue(entity, Map.class);
        final var content = documentFormatter.format(entityContentMap, documentMetadata.contentFormat(), objectMapper);
        return vectorStore.similaritySearch(
                        SearchRequest
                                .query(content)
                                .withTopK(embeddingSearchConfiguration.topK())
                                .withFilterExpression(this.excludeSameEntityExpression(id))
                                .withSimilarityThreshold(embeddingSearchConfiguration.similarityThreshold())
                )
                .stream()
                .map(this::toBaseDocument)
                .toList();
    }

    public IdGenerator getIdGenerator(Long entityId) {
        return new DefaultIdGenerator(documentMetadata.docIdPrefix(), entityId);
    }

    private Filter.Expression excludeSameEntityExpression(Long id) {
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        return b.ne(FIELD_ID, id).build();
    }

    @SneakyThrows
    protected D toBaseDocument(Document document) {
        D baseDocument = docType.getDeclaredConstructor().newInstance();
        baseDocument.setContent(document.getContent());
        baseDocument.setMetadata(document.getMetadata());
        return baseDocument;
    }

    @Autowired
    private void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    private void setEmbeddingSearchConfiguration(EmbeddingSearchConfiguration embeddingSearchConfiguration) {
        this.embeddingSearchConfiguration = embeddingSearchConfiguration;
    }
}