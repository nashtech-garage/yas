package com.yas.recommendation.vector.product.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.recommendation.dto.ProductDetailDTO;
import com.yas.recommendation.dto.RelatedProductDto;
import com.yas.recommendation.service.ProductService;
import com.yas.recommendation.vector.common.query.VectorQuery;
import com.yas.recommendation.vector.product.document.ProductDocument;
import java.util.Map;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * Service for performing related product searches using vector similarity.
 * Extends {@link VectorQuery} for {@link RelatedProductDto} results.
 */
@Service
public class RelatedProductQuery extends VectorQuery<ProductDocument, RelatedProductDto> {

    private final ObjectMapper objectMapper;
    private final ProductService productService;

    protected RelatedProductQuery(
        VectorStore vectorStore,
        ObjectMapper objectMapper,
        ProductService productService
    ) {
        super(ProductDocument.class, RelatedProductDto.class, vectorStore);
        this.objectMapper = objectMapper;
        this.productService = productService;
    }

    @Override
    public Map<String, String> getEntity(Long productId) {
        ProductDetailDTO productDetail = productService.getProductDetail(productId);
        return objectMapper.convertValue(productDetail, Map.class);
    }
}
