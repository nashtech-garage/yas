package com.yas.search.Controller;

import com.yas.search.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/storefront/product")
    public ResponseEntity<Object> findProduct(@RequestParam String name) {
        return ResponseEntity.ok(productService.findProduct(name));
    }
}
