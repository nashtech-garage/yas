package com.yas.product.controller;

import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.product.ProductVariationVm;
import com.yas.product.viewmodel.productoption.ProductOptionValueGetVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductOptionValueController {
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductRepository productRepository;

    public ProductOptionValueController(ProductOptionValueRepository productOptionValueRepository, ProductRepository productRepository) {
        this.productOptionValueRepository = productOptionValueRepository;
        this.productRepository = productRepository;
    }

    @GetMapping({"/backoffice/product-option-values"})
    public ResponseEntity<List<ProductOptionValueGetVm>> listProductOptionValues() {
        List<ProductOptionValueGetVm> productOptionGetVms = productOptionValueRepository
                .findAll().stream()
                .map(ProductOptionValueGetVm::fromModel)
                .toList();
        return ResponseEntity.ok(productOptionGetVms);
    }

    @GetMapping({"/storefront/product-option-values/{productId}"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ProductOptionValueGetVm.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    public ResponseEntity<List<ProductVariationVm>> listProductOptionValueOfProduct(@PathVariable("productId") Long productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, productId));
        List<ProductVariationVm> productVariations = productOptionValueRepository
                .findAllByProduct(product).stream()
                .map(ProductVariationVm::fromModel)
                .toList();
        return ResponseEntity.ok(productVariations);
    }
}
