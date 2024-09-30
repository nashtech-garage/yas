package com.yas.recommendation.service.query;

import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.dto.RelatedProductDto;
import com.yas.recommendation.service.ProductService;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * Service for performing related product searches using vector similarity.
 * Extends {@link VectorQueryService} for {@link RelatedProductDto} results.
 */
@Service
public class RelatedProductSearch extends VectorQueryService<RelatedProductDto> {

    protected RelatedProductSearch(
        VectorStore vectorStore,
        ProductService productService,
        EmbeddingSearchConfiguration searchConfig
    ) {
        super(RelatedProductDto.class, vectorStore, productService, searchConfig);
    }
}
