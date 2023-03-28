package com.yas.search.Controller;

import com.yas.search.document.Product;
import com.yas.search.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/storefront/product_advance")
    public ResponseEntity<List<Product>> findProductAdvance(@RequestParam String name) {
        List<Product> tmp = productService.findProductAdvance(name);
        return ResponseEntity.ok(productService.findProductAdvance(name));
    }
}
