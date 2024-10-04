package com.yas.recommendation.vector.product.store;

import com.yas.recommendation.dto.ProductDetailDTO;
import com.yas.recommendation.service.ProductService;
import com.yas.recommendation.vector.common.store.VectorStoreService;
import com.yas.recommendation.vector.product.document.ProductDocument;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

/**
 * Component for managing vector store operations specifically for {@link ProductDocument}.
 */
@Component
public class ProductVectorStoreService extends VectorStoreService<ProductDocument, ProductDetailDTO> {

    private final ProductService productService;

    public ProductVectorStoreService(VectorStore vectorStore, ProductService productService) {
        super(ProductDocument.class, vectorStore);
        this.productService = productService;
    }

    @Override
    public ProductDetailDTO getEntity(Long id) {
        return productService.getProductDetail(id);
    }
}
