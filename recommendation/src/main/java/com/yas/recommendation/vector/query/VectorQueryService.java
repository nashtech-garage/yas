package com.yas.recommendation.vector.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.vector.document.DocumentFormat;
import com.yas.recommendation.vector.document.IDocument;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.util.Assert;

/**
 * An abstract service class for performing similarity searches on a vector store.
 *
 * @param <P> the type of product result that this service handles.
 */
public abstract class VectorQueryService<D extends Document & IDocument, P> {

    @Getter
    private final Class<D> productDocType;

    @Getter
    private final Class<P> productResultType;

    private final String queryFormat;
    private final ObjectMapper objectMapper;
    private final VectorStore vectorStore;
    private final EmbeddingSearchConfiguration embeddingSearchConfiguration;

    /**
     * Constructs a VectorQueryService with the specified parameters.
     *
     * @param productResultType the class type of the product result.
     * @param vectorStore the vector store used for performing similarity searches.
     * @param embeddingSearchConfiguration the configuration settings for embedding search.
     */
    protected VectorQueryService(
        Class<D> productDocType,
        Class<P> productResultType,
        ObjectMapper objectMapper,
        VectorStore vectorStore,
        EmbeddingSearchConfiguration embeddingSearchConfiguration
    ) {
        Assert.isTrue(
            productDocType.isAnnotationPresent(DocumentFormat.class),
            "Document must be annotated by '@DocumentFormat'"
        );
        this.productDocType = productDocType;
        this.productResultType = productResultType;
        this.objectMapper = objectMapper;
        this.vectorStore = vectorStore;
        this.embeddingSearchConfiguration = embeddingSearchConfiguration;
        this.queryFormat = this.productDocType.getAnnotation(DocumentFormat.class).value();
    }

    public abstract Map<String, String> getEntity(Long productId);

    /**
     * Performs a similarity search based on a product ID.
     *
     * @param productId the ID of the product for which to perform the similarity search.
     * @return a list of product results that are similar to the specified product.
     */
    public List<P> similaritySearch(Long productId) {
        final var entityMap = getEntity(productId);
        StringSubstitutor sub = new StringSubstitutor(entityMap);
        final var query = sub.replace(queryFormat);
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
            .map(doc -> objectMapper.convertValue(doc.getMetadata(), productResultType))
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

}
