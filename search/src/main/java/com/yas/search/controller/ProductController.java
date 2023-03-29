package com.yas.search.controller;

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

    @GetMapping("/storefront/product")
    public ResponseEntity<Object> findProductByCategory(@RequestParam String categorySlug,
                                                        @RequestParam(defaultValue = "0") Integer page,
                                                        @RequestParam(defaultValue = "5") Integer size) {
        return ResponseEntity.ok(productService.findByCategoryName(categorySlug, page, size));
    }

    @GetMapping("/storefront/product_advance")
    public ResponseEntity<ProductListGetVm> findProductAdvance(@RequestParam String keyword,
                                                               @RequestParam(defaultValue = "0") Integer page,
                                                               @RequestParam(defaultValue = "5") Integer size) {
        return ResponseEntity.ok(productService.findProductAdvance(keyword, page, size));
    }
}
