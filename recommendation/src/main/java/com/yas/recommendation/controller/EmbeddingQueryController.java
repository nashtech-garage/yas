package com.yas.recommendation.controller;

import com.yas.recommendation.vector.common.query.VectorQuery;
import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.viewmodel.RelatedProductVm;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling embedding-related queries.
 */
@RestController
@RequestMapping("embedding")
public class EmbeddingQueryController {

    private final VectorQuery<ProductDocument, RelatedProductVm> relatedProductSearch;

    public EmbeddingQueryController(VectorQuery<ProductDocument, RelatedProductVm> relatedProductSearch) {
        this.relatedProductSearch = relatedProductSearch;
    }

    @GetMapping("/product/{id}/similarity")
    public List<RelatedProductVm> searchProduct(@PathVariable("id") Long productId) {
        return relatedProductSearch.similaritySearch(productId);
    }
}
