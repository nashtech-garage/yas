package com.yas.recommendation.vector.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.dto.ProductDetailDTO;
import com.yas.recommendation.dto.RelatedProductDto;
import com.yas.recommendation.service.ProductService;
import com.yas.recommendation.vector.document.ProductDocument;
import java.util.Map;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * Service for performing related product searches using vector similarity.
 * Extends {@link VectorQueryService} for {@link RelatedProductDto} results.
 */
@Service
public class RelatedProductSearch extends VectorQueryService<ProductDocument, RelatedProductDto> {

    private final ObjectMapper objectMapper;
    private final ProductService productService;

    protected RelatedProductSearch(
        VectorStore vectorStore,
        ObjectMapper objectMapper,
        ProductService productService,
        EmbeddingSearchConfiguration searchConfig
    ) {
        super(ProductDocument.class, RelatedProductDto.class, objectMapper, vectorStore, searchConfig);
        this.objectMapper = objectMapper;
        this.productService = productService;
    }

    @Override
    public Map<String, String> getEntity(Long productId) {
        ProductDetailDTO productDetail = productService.getProductDetail(productId);
        return objectMapper.convertValue(productDetail, Map.class);
    }
}
