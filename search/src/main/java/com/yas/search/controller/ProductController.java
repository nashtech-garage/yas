package com.yas.search.controller;

import com.yas.search.constant.enums.ESortType;
import com.yas.search.service.ProductService;
import com.yas.search.viewmodel.ProductListGetVm;
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
                                                               @RequestParam(required = false) String category,
                                                               @RequestParam(required = false) String attribute,
                                                               @RequestParam(required = false) Double minPrice,
                                                               @RequestParam(required = false) Double maxPrice,
                                                               @RequestParam(defaultValue = "DEFAULT") ESortType sortType) {
        return ResponseEntity.ok(productService.findProductAdvance(keyword, page, size, category, attribute, minPrice, maxPrice, sortType));
    }
}
