package com.yas.product.controller;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class ProductAttributeController {

    private final ProductAttributeRepository productAttributeRepository;
    private final ProductAttributeGroupRepository productAttributeGroupRepository;

    public ProductAttributeController(ProductAttributeRepository productAttributeRepository,  ProductAttributeGroupRepository productAttributeGroupRepository) {
        this.productAttributeRepository = productAttributeRepository;
        this.productAttributeGroupRepository = productAttributeGroupRepository;
    }

    @GetMapping({"/backoffice/product-attribute" , "/storefront/product-attribute"})
    public ResponseEntity<List<ProductAttributeGetVm>> listProductAttributes(){
        List<ProductAttributeGetVm> productAttributeGetVms = productAttributeRepository
                .findAll().stream()
                .map(ProductAttributeGetVm::fromModel)
                .toList();
        return ResponseEntity.ok(productAttributeGetVms);
    }
    @GetMapping("/backoffice/product-attribute/{id}" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "OK" , content = @Content(schema = @Schema(implementation =  ProductAttributeGetVm.class))),
            @ApiResponse(responseCode = "404" , description = "Not found" , content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    public ResponseEntity<ProductAttributeGetVm> getProductAttribute(@PathVariable("id") Long id){
       ProductAttribute productAttribute = productAttributeRepository
               .findById(id)
               .orElseThrow(()-> new NotFoundException(String.format(Constants.ERROR_CODE.PRODUCT_ATTRIBUTE_NOT_FOUND , id)));
       return ResponseEntity.ok(ProductAttributeGetVm.fromModel(productAttribute));
    }

    @PostMapping("/backoffice/product-attribute")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = ProductAttributeGetVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<ProductAttributeGetVm> createProductAttribute(@Valid @RequestBody ProductAttributePostVm productAttributePostVm, UriComponentsBuilder uriComponentsBuilder, Principal principal) {
       ProductAttribute productAttribute = new ProductAttribute();
       productAttribute.setName(productAttributePostVm.name());
       productAttribute.setCreatedBy(principal.getName());
       productAttribute.setLastModifiedBy(principal.getName());
       if(productAttributePostVm.productAttributeGroupId() != null){
           ProductAttributeGroup productAttributeGroup = productAttributeGroupRepository
                   .findById(productAttributePostVm.productAttributeGroupId())
                   .orElseThrow(() -> new BadRequestException(String.format(Constants.ERROR_CODE.PRODUCT_ATTRIBUTE_NOT_FOUND, productAttributePostVm.productAttributeGroupId())));
           productAttribute.setProductAttributeGroup(productAttributeGroup);
       }
       ProductAttribute savedProductAttribute = productAttributeRepository.saveAndFlush(productAttribute);
       ProductAttributeGetVm productAttributeGetVm = ProductAttributeGetVm.fromModel(savedProductAttribute);
       return  ResponseEntity.created(uriComponentsBuilder.replacePath("/product-attribute/{id}").buildAndExpand(savedProductAttribute.getId()).toUri())
               .body(productAttributeGetVm);
    }

    @PutMapping("/backoffice/product-attribute/{id}")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateProductAttribute(@PathVariable Long id, @Valid @RequestBody final ProductAttributePostVm productAttributePostVm) {
        ProductAttribute productAttribute = productAttributeRepository
                .findById(id)
                .orElseThrow(()-> new NotFoundException(String.format(Constants.ERROR_CODE.PRODUCT_ATTRIBUTE_NOT_FOUND, id)));
        productAttribute.setName(productAttributePostVm.name());
        if(productAttributePostVm.productAttributeGroupId() != null){
            ProductAttributeGroup productAttributeGroup = productAttributeGroupRepository
                    .findById(productAttributePostVm.productAttributeGroupId())
                    .orElseThrow(() -> new BadRequestException(String.format(Constants.ERROR_CODE.PRODUCT_ATTRIBUTE_NOT_FOUND, productAttributePostVm.productAttributeGroupId())));
            productAttribute.setProductAttributeGroup(productAttributeGroup);
        }
        productAttributeRepository.saveAndFlush(productAttribute);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/backoffice/product-attribute/{id}")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteProductAttribute(@PathVariable Long id){
        ProductAttribute productAttribute = productAttributeRepository
                .findById(id)
                .orElseThrow(()->new NotFoundException(String.format(Constants.ERROR_CODE.PRODUCT_ATTRIBUTE_NOT_FOUND, id)));
        if(!productAttribute.getAttributeValues().isEmpty())
            throw new BadRequestException("Please make sure this Product Attribute doesn't exist in any Product Attribute Values");
        productAttributeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
