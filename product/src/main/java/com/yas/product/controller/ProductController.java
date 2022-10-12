package com.yas.product.controller;

import com.yas.product.service.ProductService;
import com.yas.product.viewmodel.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

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


    @PostMapping(path = "/backoffice/products", consumes = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = ProductGetDetailVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<ProductGetDetailVm> createProduct(@RequestPart("productDetails") ProductPostVm productPostVm,
            @RequestPart("files") List<MultipartFile> files, UriComponentsBuilder uriComponentsBuilder) {
        ProductGetDetailVm productGetDetailVm = productService.createProduct(productPostVm, files);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/products/{id}").buildAndExpand(productGetDetailVm.id()).toUri())
                .body(productGetDetailVm);
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
    public ResponseEntity<List<ProductThumbnailVm>> getFeaturedProducts() {
        return ResponseEntity.ok(productService.getFeaturedProducts());
    }

    @GetMapping("/storefront/brand/{brandSlug}/products")
    public ResponseEntity<List<ProductThumbnailVm>> getProductsByBrand(@PathVariable String brandSlug) {
        return ResponseEntity.ok(productService.getProductsByBrand(brandSlug));
    }

    @GetMapping("/storefront/category/{categorySlug}/products")
    public ResponseEntity<ProductListGetFromCategoryVm> getProductsByCategory(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "2", required = false) int pageSize,
            @PathVariable String categorySlug
    ) {
        return ResponseEntity.ok(productService.getProductsFromCategory(pageNo, pageSize,categorySlug));
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

    @GetMapping("/storefront/products/featured/{productId}")
    public ResponseEntity<ProductThumbnailVm> getFeaturedProductsById(@PathVariable long productId) {
        return ResponseEntity.ok(productService.getFeaturedProductsById(productId));
    }
}
