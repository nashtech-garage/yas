package com.yas.recommendation.controller;

import com.yas.recommendation.dto.RelatedProductDto;
import com.yas.recommendation.service.query.VectorQueryService;
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

    private final VectorQueryService<RelatedProductDto> relatedProductSearch;

    public EmbeddingQueryController(VectorQueryService<RelatedProductDto> relatedProductSearch) {
        this.relatedProductSearch = relatedProductSearch;
    }

    @GetMapping("/product/{id}/similarity")
    public List<RelatedProductDto> searchProduct(@PathVariable("id") Long productId) {
        return relatedProductSearch.similaritySearch(productId);
    }

}
