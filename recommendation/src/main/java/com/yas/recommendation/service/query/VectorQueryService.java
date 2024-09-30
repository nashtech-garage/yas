package com.yas.recommendation.service.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.service.ProductService;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

/**
 * An abstract service class for performing similarity searches on a vector store.
 *
 * @param <P> the type of product result that this service handles.
 */
public abstract class VectorQueryService<P> {

    private final Class<P> productResultType;
    private final ObjectMapper objectMapper;
    private final VectorStore vectorStore;
    private final ProductService productService;
    private final EmbeddingSearchConfiguration embeddingSearchConfiguration;

    /**
     * Constructs a VectorQueryService with the specified parameters.
     *
     * @param productResultType the class type of the product result.
     * @param vectorStore the vector store used for performing similarity searches.
     * @param productService the product service used for product-related operations.
     * @param embeddingSearchConfiguration the configuration settings for embedding search.
     */
    protected VectorQueryService(
        Class<P> productResultType,
        ObjectMapper objectMapper,
        VectorStore vectorStore,
        ProductService productService,
        EmbeddingSearchConfiguration embeddingSearchConfiguration
    ) {
        this.productResultType = productResultType;
        this.objectMapper = objectMapper;
        this.vectorStore = vectorStore;
        this.productService = productService;
        this.embeddingSearchConfiguration = embeddingSearchConfiguration;
    }

    /**
     * Performs a similarity search based on a product ID.
     *
     * @param productId the ID of the product for which to perform the similarity search.
     * @return a list of product results that are similar to the specified product.
     */
    public List<P> similaritySearch(Long productId) {
        // TODO: should replace by productService.buildFormattedProduct(productId) once productService done
        final var query = "Smart Home device";
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
