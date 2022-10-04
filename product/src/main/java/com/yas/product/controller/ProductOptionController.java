package com.yas.product.controller;

import com.yas.product.exception.NotFoundException;
import com.yas.product.model.ProductOption;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.viewmodel.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
public class ProductOptionController {
    private final ProductOptionRepository productOptionRepository;
    public ProductOptionController(ProductOptionRepository productOptionRepository){
        this.productOptionRepository = productOptionRepository;
    }

    @GetMapping({"/backoffice/product-options"})
    public ResponseEntity<List<ProductOptionGetVm>> listProductOption(){
        List<ProductOptionGetVm> productOptionGetVms = productOptionRepository
                .findAll().stream()
                .map(ProductOptionGetVm::fromModel)
                .toList();
        return ResponseEntity.ok(productOptionGetVms);
    }

    @GetMapping("/backoffice/product-options/{id}" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "OK" , content = @Content(schema = @Schema(implementation =  ProductAttributeGetVm.class))),
            @ApiResponse(responseCode = "404" , description = "Not found" , content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    public ResponseEntity<ProductOptionGetVm> getProductOption(@PathVariable("id") Long id){
        ProductOption productOption = productOptionRepository
                .findById(id)
                .orElseThrow(()-> new NotFoundException(String.format("Product option %s is not found" , id)));
        return ResponseEntity.ok(ProductOptionGetVm.fromModel(productOption));
    }

    @PostMapping("/backoffice/product-options")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema( implementation = ProductOptionGetVm.class))),
    })
    public ResponseEntity<ProductOptionGetVm> createProductOption(@Valid @RequestBody ProductOptionPostVm productOptionPostVm, Principal principal, UriComponentsBuilder uriComponentsBuilder) {
        ProductOption productOption = new ProductOption();
        productOption.setName(productOptionPostVm.name());
        productOption.setCreatedBy(principal.getName());
        productOption.setLastModifiedBy(principal.getName());
        ProductOption savedProductOption = productOptionRepository.saveAndFlush(productOption);
        ProductOptionGetVm productOptionGetVm = ProductOptionGetVm.fromModel(savedProductOption);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/product-options/{id}").buildAndExpand(savedProductOption.getId()).toUri())
                .body(productOptionGetVm);
    }
    @PutMapping("/backoffice/product-options/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    public ResponseEntity<Void> updateProductOption(@PathVariable Long id, @Valid @RequestBody ProductOptionPostVm productOptionPostVm, Principal principal){
        ProductOption productOption = productOptionRepository
                .findById(id)
                .orElseThrow(()-> new NotFoundException(String.format("Product option %s is not found", id)));
        productOption.setName(productOptionPostVm.name());
        productOption.setLastModifiedBy(principal.getName());
        productOption.setLastModifiedOn(ZonedDateTime.now());
        productOptionRepository.saveAndFlush(productOption);
        return ResponseEntity.noContent().build();
    }

}
