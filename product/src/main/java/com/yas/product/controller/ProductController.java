package com.yas.product.controller;

import com.yas.product.model.enumeration.FilterExistInWhSelection;
import com.yas.product.service.ProductService;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.product.ProductDetailGetVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductEsDetailVm;
import com.yas.product.viewmodel.product.ProductExportingDetailVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductInfoVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductVariationGetVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/backoffice/export/products")
    public ResponseEntity<List<ProductExportingDetailVm>> exportProducts(
        @RequestParam(value = "product-name", defaultValue = "", required = false) String productName,
        @RequestParam(value = "brand-name", defaultValue = "", required = false) String brandName
    ) {
        return ResponseEntity.ok(productService.exportProducts(productName, brandName));
    }

    @PostMapping(path = "/backoffice/products", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created",
            content = @Content(schema = @Schema(implementation = ProductGetDetailVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))
    })
    public ResponseEntity<ProductGetDetailVm> createProduct(@Valid @RequestBody ProductPostVm productPostVm) {
        ProductGetDetailVm productGetDetailVm = productService.createProduct(productPostVm);
        return new ResponseEntity<>(productGetDetailVm, HttpStatus.CREATED);
    }

    @PutMapping(path = "/backoffice/products/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Updated"),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))
    })
    public ResponseEntity<Void> updateProduct(@PathVariable long id, @Valid @RequestBody ProductPutVm productPutVm) {
        productService.updateProduct(id, productPutVm);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/storefront/products/featured")
    public ResponseEntity<ProductFeatureGetVm> getFeaturedProducts(
        @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
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

    @GetMapping("/storefront/products/list-featured")
    public ResponseEntity<List<ProductThumbnailGetVm>> getFeaturedProductsById(
            @RequestParam("productId") List<Long> productIds
    ) {
        return ResponseEntity.ok(productService.getFeaturedProductsById(productIds));
    }

    @GetMapping("/storefront/product/{slug}")
    public ResponseEntity<ProductDetailGetVm> getProductDetail(@PathVariable("slug") String slug) {
        return ResponseEntity.ok(productService.getProductDetail(slug));
    }

    @DeleteMapping("/backoffice/products/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
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
        return ResponseEntity.ok(productService.getProductsByMultiQuery(
            pageNo, pageSize, productName, categorySlug, startPrice, endPrice
        ));
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get product variations by parent id successfully",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ProductVariationGetVm.class)))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVm.class)))
    })
    @GetMapping({"/storefront/product-variations/{id}", "/backoffice/product-variations/{id}"})
    public ResponseEntity<List<ProductVariationGetVm>> getProductVariationsByParentId(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductVariationsByParentId(id));
    }

    @GetMapping("/storefront/productions/{id}/slug")
    public ResponseEntity<ProductSlugGetVm> getProductSlug(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductSlug(id));
    }

    @GetMapping("/storefront/products-es/{productId}")
    public ResponseEntity<ProductEsDetailVm> getProductEsDetailById(@PathVariable long productId) {
        return ResponseEntity.ok(productService.getProductEsDetailById(productId));
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get related products by product id successfully",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ProductVariationGetVm.class)))),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVm.class)))
    })
    @GetMapping("/backoffice/products/related-products/{id}")
    public ResponseEntity<List<ProductListVm>> getRelatedProductsBackoffice(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getRelatedProductsBackoffice(id));
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get related products by product id successfully",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ProductVariationGetVm.class)))),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVm.class)))
    })
    @GetMapping("/storefront/products/related-products/{id}")
    public ResponseEntity<ProductsGetVm> getRelatedProductsStorefront(
        @PathVariable Long id,
        @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
        @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize
    ) {
        return ResponseEntity.ok(productService.getRelatedProductsStorefront(id, pageNo, pageSize));
    }

    @GetMapping("/backoffice/products/for-warehouse")
    public ResponseEntity<List<ProductInfoVm>> getProductsForWarehouse(
        @RequestParam String name, @RequestParam String sku,
        @RequestParam(required = false) List<Long> productIds,
        @RequestParam(required = false) FilterExistInWhSelection selection
    ) {
        return ResponseEntity.ok(productService.getProductsForWarehouse(name, sku, productIds, selection));
    }

    @PutMapping(path = "/backoffice/products/update-quantity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Updated"),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateProductQuantity(
            @Valid @RequestBody List<ProductQuantityPostVm> productQuantityPostVms
    ) {
        productService.updateProductQuantity(productQuantityPostVms);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/backoffice/products/subtract-quantity", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Updated"),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> subtractProductQuantity(
            @Valid @RequestBody List<ProductQuantityPutVm> productQuantityPutVm
    ) {
        productService.subtractStockQuantity(productQuantityPutVm);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/backoffice/products/by-ids")
    public ResponseEntity<List<ProductListVm>> getProductByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(productService.getProductByIds(ids));
    }
}
