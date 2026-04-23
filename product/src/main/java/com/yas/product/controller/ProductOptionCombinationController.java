package com.yas.product.controller;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.product.ProductOptionCombinationGetVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductOptionCombinationController {

    private final ProductOptionCombinationRepository productOptionCombinationRepository;
    private final ProductRepository productRepository;

    public ProductOptionCombinationController(
            ProductOptionCombinationRepository productOptionCombinationRepository,
            ProductRepository productRepository
    ) {
        this.productOptionCombinationRepository = productOptionCombinationRepository;
        this.productRepository = productRepository;
    }

    @GetMapping({"/storefront/product-option-combinations/{productId}/values"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = ProductOptionCombinationGetVm.class))),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    public ResponseEntity<List<ProductOptionCombinationGetVm>> listProductOptionValueOfProduct(
        @PathVariable("productId") Long productId
    ) {

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.PRODUCT_NOT_FOUND, productId));

        List<ProductOptionCombinationGetVm> productOptionCombinationGetVms = productOptionCombinationRepository
                .findAllByParentProductId(product.getId()).stream()
                .map(ProductOptionCombinationGetVm::fromModel)
                .toList();

        return ResponseEntity.ok(new ArrayList<>(productOptionCombinationGetVms
            .stream().collect(Collectors.toMap(
                p -> Arrays.asList(p.productOptionId(), p.productOptionValue()),
                p -> p, (existing, replacement) -> existing
            )).values()));
    }
}
