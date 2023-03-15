package com.yas.product.controller;

import com.yas.product.service.ProductService;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.product.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/backoffice/products")
    public ResponseEntity<ProductListGetVm> listProducts(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
            @RequestParam(value = "product-name", defaultValue = "", required = false) String productName,
            @RequestParam(value = "brand-name", defaultValue = "", required = false) String brandName
    ) {
        return ResponseEntity.ok(productService.getProductsWithFilter(pageNo, pageSize, productName, brandName));
    }


    @PostMapping(path = "/backoffice/products", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = ProductGetDetailVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<ProductGetDetailVm> createProduct(@Valid @RequestBody ProductPostVm productPostVm) {
        ProductGetDetailVm productGetDetailVm = productService.createProduct(productPostVm);
        return new ResponseEntity<>(productGetDetailVm, HttpStatus.CREATED);
    }

    @PutMapping(path = "/backoffice/products/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateProduct(@PathVariable long id, @Valid @RequestBody ProductPutVm productPutVm) {
        productService.updateProduct(id, productPutVm);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/storefront/products/featured")
    public ResponseEntity<ProductFeatureGetVm> getFeaturedProducts(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo, @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return ResponseEntity.ok(productService.getListFeaturedProducts(pageNo, pageSize));
    }

    @GetMapping("/storefront/brand/{brandSlug}/products")
    public ResponseEntity<List<ProductThumbnailVm>> getProductsByBrand(@PathVariable String brandSlug) {
        return ResponseEntity.ok(productService.getProductsByBrand(brandSlug));
    }

    @GetMapping({"/storefront/category/{categorySlug}/products", "/backoffice/category/{categorySlug}/products"})
    public ResponseEntity<ProductListGetFromCategoryVm> getProductsByCategory(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "2", required = false) int pageSize,
            @PathVariable String categorySlug
    ) {
        return ResponseEntity.ok(productService.getProductsFromCategory(pageNo, pageSize, categorySlug));
    }

    @GetMapping("/backoffice/products/{productId}")
    public ResponseEntity<ProductDetailVm> getProductById(@PathVariable long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/storefront/products/{slug}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = ProductDetailVm.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<ProductDetailVm> getProduct(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getProduct(slug));
    }

    @GetMapping("/storefront/products/list-featured")
    public ResponseEntity<List<ProductThumbnailVm>> getFeaturedProductsById(@RequestParam("productId") List<Long> productIds) {
        return ResponseEntity.ok(productService.getFeaturedProductsById(productIds));
    }

    @GetMapping("/storefront/product/{slug}")
    public ProductDetailGetVm getProductDetail(@PathVariable("slug") String slug) {
        return productService.getProductDetail(slug);
    }

    @DeleteMapping("/backoffice/products/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/storefront/products")
    public ResponseEntity<ProductsGetVm> getProductsByMultiQuery(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
            @RequestParam(value = "productName", defaultValue = "", required = false) String productName,
            @RequestParam(value = "categorySlug", defaultValue = "", required = false) String categorySlug,
            @RequestParam(value = "startPrice", defaultValue = "", required = false) Double startPrice,
            @RequestParam(value = "endPrice", defaultValue = "", required = false) Double endPrice
    ) {
        return ResponseEntity.ok(productService.getProductsByMultiQuery(pageNo, pageSize, productName, categorySlug, startPrice, endPrice));
    }
}
