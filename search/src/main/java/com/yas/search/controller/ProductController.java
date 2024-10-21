package com.yas.search.controller;

import com.yas.search.constant.enums.SortType;
import com.yas.search.model.ProductCriteriaDto;
import com.yas.search.service.ProductService;
import com.yas.search.viewmodel.ProductListGetVm;
import com.yas.search.viewmodel.ProductNameListVm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/storefront/catalog-search")
    public ResponseEntity<ProductListGetVm> findProductAdvance(@RequestParam(defaultValue = "") String keyword,
                                                               @RequestParam(defaultValue = "0") Integer page,
                                                               @RequestParam(defaultValue = "12") Integer size,
                                                               @RequestParam(required = false) String brand,
                                                               @RequestParam(required = false) String category,
                                                               @RequestParam(required = false) String attribute,
                                                               @RequestParam(required = false) Double minPrice,
                                                               @RequestParam(required = false) Double maxPrice,
                                                               @RequestParam(required = false) Double minRating,
                                                               @RequestParam(required = false) Double maxRating,
                                                               @RequestParam(defaultValue = "DEFAULT")
                                                               SortType sortType) {
        return ResponseEntity.ok(productService.findProductAdvance(
            new ProductCriteriaDto(
                keyword, page, size, brand, category, attribute, minPrice, maxPrice, minRating, maxRating, sortType
            )
        ));
    }

    @GetMapping("/storefront/search_suggest")
    public ResponseEntity<ProductNameListVm> productSearchAutoComplete(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.autoCompleteProductName(keyword));
    }
}
