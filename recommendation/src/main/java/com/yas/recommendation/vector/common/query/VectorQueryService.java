package com.yas.recommendation.vector.common.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.vector.common.document.BaseDocument;
import com.yas.recommendation.vector.common.document.DocumentMetadata;
import com.yas.recommendation.vector.common.formatter.DocumentFormatter;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Abstract service for performing similarity searches on a vector store.
 *
 * @param <D> the document type, must be annotated with {@link DocumentMetadata},
 *           to know how the query will be formatted
 * @param <P> the product result type handled by the service.
 */
public abstract class VectorQueryService<D  extends BaseDocument, P> {

    private ObjectMapper objectMapper;
    private EmbeddingSearchConfiguration embeddingSearchConfiguration;

    @Getter
    private final Class<D> docType;

    @Getter
    private final Class<P> resultType;

    private final VectorStore vectorStore;
    private final DocumentMetadata documentMetadata;
    private final DocumentFormatter documentFormatter;

    /**
     * Constructor for VectorQueryService.
     *
     * @param docType the document class type.
     * @param resultType the result class type.
     */
    @SneakyThrows
    protected VectorQueryService(
        Class<D> docType,
        Class<P> resultType,
        VectorStore vectorStore
    ) {
        Assert.isTrue(
            docType.isAnnotationPresent(DocumentMetadata.class),
            "Document must be annotated by '@DocumentFormat'"
        );
        this.docType = docType;
        this.resultType = resultType;
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
    public abstract Map<String, String> getEntity(Long id);

    /**
     * Performs a similarity search based on a product ID.
     *
     * @param id the ID of the product for which to perform the similarity search.
     * @return a list of product results that are similar to the specified product.
     */
    public List<P> similaritySearch(Long id) {
        final var entityMap = getEntity(id);
        final var query = documentFormatter.format(entityMap, documentMetadata.contentFormat());
        return toResult(doSimilaritySearch(SearchRequest.query(query)));
    }

    /**
     * Converts a list of documents to a list of product results.
     *
     * @param documents the list of documents to convert.
     * @return a list of product results corresponding to the provided documents.
     */
    protected List<P> toResult(List<Document> documents) {
        return documents
            .stream()
            .filter(doc -> doc.getMetadata() != null)
            .map(doc -> objectMapper.convertValue(doc.getMetadata(), resultType))
            .toList();
    }

    private List<Document> doSimilaritySearch(SearchRequest searchRequest) {
        loadDefaultQueryAttributes(searchRequest);
        return vectorStore.similaritySearch(searchRequest);
    }

    private void loadDefaultQueryAttributes(SearchRequest searchRequest) {
        searchRequest.withTopK(embeddingSearchConfiguration.topK());
        searchRequest.withSimilarityThreshold(embeddingSearchConfiguration.similarityThreshold());
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setEmbeddingSearchConfiguration(EmbeddingSearchConfiguration embeddingSearchConfiguration) {
        this.embeddingSearchConfiguration = embeddingSearchConfiguration;
    }
}
