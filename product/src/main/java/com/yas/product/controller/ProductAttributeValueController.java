package com.yas.product.controller;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.repository.ProductAttributeValueRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeValueGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeValuePostVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class ProductAttributeValueController {

    private final ProductAttributeValueRepository productAttributeValueRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductRepository productRepository;

    public ProductAttributeValueController(
            ProductAttributeValueRepository productAttributeValueRepository,
            ProductAttributeRepository productAttributeRepository,
            ProductRepository productRepository
    ) {
        this.productAttributeValueRepository = productAttributeValueRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.productRepository = productRepository;
    }

    @GetMapping({"/backoffice/product-attribute-value/{productId}"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
                content = @Content(schema = @Schema(implementation = ProductAttributeValueGetVm.class))),
        @ApiResponse(responseCode = "404", description = "Not found",
                content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    public ResponseEntity<List<ProductAttributeValueGetVm>> listProductAttributeValuesByProductId(
            @PathVariable("productId") Long productId
    ) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new BadRequestException(Constants.ErrorCode.PRODUCT_NOT_FOUND, productId));
        List<ProductAttributeValueGetVm> productAttributeValueGetVms = productAttributeValueRepository
                .findAllByProduct(product).stream()
                .map(ProductAttributeValueGetVm::fromModel)
                .toList();
        return ResponseEntity.ok(productAttributeValueGetVms);
    }

    @PutMapping("/backoffice/product-attribute-value/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content"),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateProductAttributeValue(
            @PathVariable Long id,
            @Valid @RequestBody final ProductAttributeValuePostVm productAttributeValuePostVm
    ) {
        ProductAttributeValue productAttributeValue = productAttributeValueRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.PRODUCT_ATTRIBUTE_VALUE_IS_NOT_FOUND, id));
        productAttributeValue.setValue(productAttributeValuePostVm.value());
        productAttributeValueRepository.save(productAttributeValue);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/backoffice/product-attribute-value")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created",
            content = @Content(schema = @Schema(implementation = ProductAttributeValueGetVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<ProductAttributeValueGetVm> createProductAttributeValue(
            @Valid @RequestBody ProductAttributeValuePostVm productAttributeValuePostVm,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        ProductAttributeValue productAttributeValue = new ProductAttributeValue();
        if (productAttributeValuePostVm.productId() != null) {
            Product product = productRepository
                .findById(productAttributeValuePostVm.productId())
                .orElseThrow(() -> new BadRequestException(Constants.ErrorCode.PRODUCT_NOT_FOUND,
                    productAttributeValuePostVm.productId()));
            productAttributeValue.setProduct(product);
        }
        if (productAttributeValuePostVm.productAttributeId() != null) {
            ProductAttribute productAttribute = productAttributeRepository
                .findById(productAttributeValuePostVm.productAttributeId())
                .orElseThrow(() -> new BadRequestException(Constants.ErrorCode.PRODUCT_ATTRIBUTE_IS_NOT_FOUND,
                    productAttributeValuePostVm.productAttributeId()));
            productAttributeValue.setProductAttribute(productAttribute);
        }
        productAttributeValue.setValue(productAttributeValuePostVm.value());
        ProductAttributeValue savedProductAttributeValue
            = productAttributeValueRepository.save(productAttributeValue);
        ProductAttributeValueGetVm productAttributeValueGetVm
            = ProductAttributeValueGetVm.fromModel(savedProductAttributeValue);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/product-attribute-value/{id}")
            .buildAndExpand(savedProductAttributeValue.getId()).toUri())
                .body(productAttributeValueGetVm);
    }

    @DeleteMapping("/backoffice/product-attribute-value/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content"),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteProductAttributeValueById(@PathVariable Long id) {
        Optional<ProductAttributeValue> productAttributeValue = productAttributeValueRepository.findById(id);
        if (productAttributeValue.isEmpty()) {
            throw new NotFoundException(Constants.ErrorCode.PRODUCT_ATTRIBUTE_VALUE_IS_NOT_FOUND, id);
        }
        productAttributeValueRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
