package com.yas.recommendation.vector.product.store;

import com.yas.recommendation.viewmodel.ProductDetailVm;
import com.yas.recommendation.service.ProductService;
import com.yas.recommendation.vector.common.store.SimpleVectorRepository;
import com.yas.recommendation.vector.product.document.ProductDocument;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

/**
 * Component for managing vector store operations specifically for {@link ProductDocument}.
 */
@Component
public class ProductVectorRepository extends SimpleVectorRepository<ProductDocument, ProductDetailVm> {

    private final ProductService productService;

    public ProductVectorRepository(VectorStore vectorStore, ProductService productService) {
        super(ProductDocument.class, vectorStore);
        this.productService = productService;
    }

    @Override
    public ProductDetailVm getEntity(Long id) {
        return productService.getProductDetail(id);
    }
}
