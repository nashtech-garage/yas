package com.yas.product.controller;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.repository.ProductAttributeValueRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ProductAttributeValueController {

    private final ProductAttributeValueRepository productAttributeValueRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductRepository productRepository;

    public ProductAttributeValueController(ProductAttributeValueRepository productAttributeValueRepository, ProductAttributeRepository productAttributeRepository,ProductRepository productRepository){
        this.productAttributeValueRepository = productAttributeValueRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.productRepository = productRepository;
    }

    @GetMapping({"/backoffice/product-attribute-value/{productId}"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "OK" , content = @Content(schema = @Schema(implementation =  ProductAttributeValueGetVm.class))),
            @ApiResponse(responseCode = "404" , description = "Not found" , content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    public ResponseEntity<List<ProductAttributeValueGetVm>> listProductAttributeValuesByProductId(@PathVariable("productId") Long productId){
        Product product = productRepository
                 .findById(productId)
                 .orElseThrow(() -> new BadRequestException(String.format("Product %s is not found",productId)));
        List<ProductAttributeValueGetVm> productAttributeValueGetVms = productAttributeValueRepository
                .findAllByProduct(product).stream()
                .map(ProductAttributeValueGetVm::fromModel)
                .toList();
        return ResponseEntity.ok(productAttributeValueGetVms);
    }

    @PutMapping("/backoffice/product-attribute-value/{id}")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateProductAttributeValue(@PathVariable Long id, @Valid @RequestBody final ProductAttributeValuePostVm productAttributeValuePostVm) {
        ProductAttributeValue productAttributeValue = productAttributeValueRepository
                .findById(id)
                .orElseThrow(()-> new NotFoundException(String.format("Product attribute value %s is not found", id)));
        productAttributeValue.setValue(productAttributeValuePostVm.value());
        productAttributeValueRepository.saveAndFlush(productAttributeValue);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/backoffice/product-attribute-value")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = ProductAttributeValueGetVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<ProductAttributeValueGetVm> createProductAttributeValue(@Valid @RequestBody ProductAttributeValuePostVm productAttributeValuePostVm, UriComponentsBuilder uriComponentsBuilder) {
        ProductAttributeValue productAttributeValue = new ProductAttributeValue();
        if(productAttributeValuePostVm.ProductId() != null){
            Product product = productRepository
                    .findById(productAttributeValuePostVm.ProductId())
                    .orElseThrow(() -> new BadRequestException(String.format("Product %s is not found", productAttributeValuePostVm.ProductId())));
            productAttributeValue.setProduct(product);
        }
        if(productAttributeValuePostVm.productAttributeId() != null){
            ProductAttribute productAttribute = productAttributeRepository
                    .findById(productAttributeValuePostVm.productAttributeId())
                    .orElseThrow(() -> new BadRequestException(String.format("Product Attribute %s is not found",productAttributeValuePostVm.productAttributeId())));
            productAttributeValue.setProductAttribute(productAttribute);
        }
        productAttributeValue.setValue(productAttributeValuePostVm.value());
        ProductAttributeValue savedProductAttributeValue = productAttributeValueRepository.saveAndFlush(productAttributeValue);
        ProductAttributeValueGetVm productAttributeValueGetVm = ProductAttributeValueGetVm.fromModel(savedProductAttributeValue);
        return  ResponseEntity.created(uriComponentsBuilder.replacePath("/product-attribute-value/{id}").buildAndExpand(savedProductAttributeValue.getId()).toUri())
                .body(productAttributeValueGetVm);
    }
}
