package com.yas.recommendation.vector.product.query;

import com.yas.recommendation.dto.RelatedProductDto;
import com.yas.recommendation.vector.common.query.VectorQuery;
import com.yas.recommendation.vector.product.document.ProductDocument;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * Service for performing related product searches using vector similarity.
 * Extends {@link VectorQuery} for {@link RelatedProductDto} results.
 */
@Service
public class RelatedProductQuery extends VectorQuery<ProductDocument, RelatedProductDto> {

    protected RelatedProductQuery(VectorStore vectorStore) {
        super(ProductDocument.class, RelatedProductDto.class);
    }
}
