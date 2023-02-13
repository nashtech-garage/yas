package com.yas.product.controller;

import com.yas.product.exception.NotFoundException;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.viewmodel.ErrorVm;
import com.yas.product.viewmodel.ProductAttributeGroupPostVm;
import com.yas.product.viewmodel.ProductAttributeGroupVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
public class ProductAttributeGroupController {
    private final String productAttributeGroupNotFoundMessage = "Product attribute group %s is not found";

    private final ProductAttributeGroupRepository productAttributeGroupRepository;

    public ProductAttributeGroupController(ProductAttributeGroupRepository productAttributeGroupRepository) {
        this.productAttributeGroupRepository = productAttributeGroupRepository;
    }

    @GetMapping("/backoffice/product-attribute-groups")
    public ResponseEntity<List<ProductAttributeGroupVm>> listProductAttributeGroups() {
        List<ProductAttributeGroupVm> productAttributeGroupVms = productAttributeGroupRepository.findAll().stream()
                .map(ProductAttributeGroupVm::fromModel)
                .toList();
        return ResponseEntity.ok(productAttributeGroupVms);
    }

    @GetMapping("/backoffice/product-attribute-groups/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = ProductAttributeGroupVm.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    public ResponseEntity<ProductAttributeGroupVm> getProductAttributeGroup(@PathVariable("id") Long id) {
        ProductAttributeGroup productAttributeGroup = productAttributeGroupRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(productAttributeGroupNotFoundMessage, id)));
        return ResponseEntity.ok(ProductAttributeGroupVm.fromModel(productAttributeGroup));
    }

    @PostMapping("/backoffice/product-attribute-groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = ProductAttributeGroupVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<ProductAttributeGroupVm> createProductAttributeGroup(@Valid @RequestBody ProductAttributeGroupPostVm productAttributeGroupPostVm, UriComponentsBuilder uriComponentsBuilder) {
        ProductAttributeGroup productAttributeGroup = productAttributeGroupPostVm.toModel();
        productAttributeGroupRepository.save(productAttributeGroup);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/product-attribute-groups/{id}").buildAndExpand(productAttributeGroup.getId()).toUri())
                .body(ProductAttributeGroupVm.fromModel(productAttributeGroup));
    }

    @PutMapping("/backoffice/product-attribute-groups/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateProductAttributeGroup(@PathVariable Long id, @Valid @RequestBody final ProductAttributeGroupPostVm productAttributeGroupPostVm) {
        ProductAttributeGroup productAttributeGroup = productAttributeGroupRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(productAttributeGroupNotFoundMessage, id)));
        productAttributeGroup.setName(productAttributeGroupPostVm.name());
        productAttributeGroupRepository.save(productAttributeGroup);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/backoffice/product-attribute-groups/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteProductAttributeGroup(@PathVariable Long id) {
        productAttributeGroupRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(productAttributeGroupNotFoundMessage, id)));
        productAttributeGroupRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
