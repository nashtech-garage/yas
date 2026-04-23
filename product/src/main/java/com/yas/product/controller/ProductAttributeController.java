package com.yas.product.controller;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.constants.PageableConstant;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.service.ProductAttributeService;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeListGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributePostVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class ProductAttributeController {

    private final ProductAttributeService productAttributeService;
    private final ProductAttributeRepository productAttributeRepository;

    public ProductAttributeController(ProductAttributeRepository productAttributeRepository,
                                      ProductAttributeService productAttributeService) {
        this.productAttributeRepository = productAttributeRepository;
        this.productAttributeService = productAttributeService;
    }

    @GetMapping({"/backoffice/product-attribute", "/storefront/product-attribute"})
    public ResponseEntity<List<ProductAttributeGetVm>> listProductAttributes() {
        List<ProductAttributeGetVm> productAttributeGetVms = productAttributeRepository
                .findAll().stream()
                .map(ProductAttributeGetVm::fromModel)
                .toList();
        return ResponseEntity.ok(productAttributeGetVms);
    }

    @GetMapping({"/backoffice/product-attribute/paging", "/storefront/product-attribute/paging"})
    public ResponseEntity<ProductAttributeListGetVm> getPageableProductAttributes(
            @RequestParam(value = "pageNo", defaultValue = PageableConstant.DEFAULT_PAGE_NUMBER, required = false)
            int pageNo,
            @RequestParam(value = "pageSize", defaultValue = PageableConstant.DEFAULT_PAGE_SIZE, required = false)
            int pageSize) {

        return ResponseEntity.ok(productAttributeService.getPageableProductAttributes(pageNo, pageSize));
    }

    @GetMapping("/backoffice/product-attribute/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = ProductAttributeGetVm.class))),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    public ResponseEntity<ProductAttributeGetVm> getProductAttribute(@PathVariable("id") Long id) {
        ProductAttribute productAttribute = productAttributeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                    String.format(Constants.ErrorCode.PRODUCT_ATTRIBUTE_NOT_FOUND, id)));
        return ResponseEntity.ok(ProductAttributeGetVm.fromModel(productAttribute));
    }

    @PostMapping("/backoffice/product-attribute")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created",
            content = @Content(schema = @Schema(implementation = ProductAttributeGetVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<ProductAttributeGetVm> createProductAttribute(
            @Valid @RequestBody ProductAttributePostVm productAttributePostVm,
            UriComponentsBuilder uriComponentsBuilder,
            Principal principal
    ) {
        ProductAttribute savedProductAttribute = productAttributeService.save(productAttributePostVm);
        ProductAttributeGetVm productAttributeGetVm = ProductAttributeGetVm.fromModel(savedProductAttribute);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/product-attribute/{id}")
            .buildAndExpand(savedProductAttribute.getId()).toUri())
                .body(productAttributeGetVm);
    }

    @PutMapping("/backoffice/product-attribute/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content"),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateProductAttribute(
            @PathVariable Long id,
            @Valid @RequestBody final ProductAttributePostVm productAttributePostVm
    ) {
        productAttributeService.update(productAttributePostVm, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/backoffice/product-attribute/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content"),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteProductAttribute(@PathVariable Long id) {
        ProductAttribute productAttribute = productAttributeRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(
                String.format(Constants.ErrorCode.PRODUCT_ATTRIBUTE_NOT_FOUND, id)));
        if (!productAttribute.getAttributeValues().isEmpty()) {
            throw new BadRequestException(Constants.ErrorCode.THIS_PROD_ATTRI_NOT_EXIST_IN_ANY_PROD_ATTRI);
        }
        productAttributeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
