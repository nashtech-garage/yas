package com.yas.product.controller;

import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
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
public class ProductVariationController {
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionCombinationRepository productOptioncombinationRepository;
    public ProductVariationController(ProductRepository productRepository, ProductOptionRepository productOptionRepository, ProductOptionCombinationRepository productOptioncombinationRepository){
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
        this.productOptioncombinationRepository = productOptioncombinationRepository;
    }
    @GetMapping({"/backoffice/product-option-combinations"})
    public ResponseEntity<List<ProductOptionCombinationGetVm>> listProductOptionCombination(){
        List<ProductOptionCombinationGetVm> productOptionCombinationGetVms = productOptioncombinationRepository
                .findAll().stream()
                .map(ProductOptionCombinationGetVm::fromModel)
                .toList();
        return ResponseEntity.ok(productOptionCombinationGetVms);
    }
    @PostMapping("/backoffice/product-option-combinations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema( implementation = ProductOptionCombinationGetVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))
    })
    public ResponseEntity<ProductOptionCombinationGetVm> createProductOptionValue(@Valid @RequestBody ProductOptionCombinationPostVm productOptionCombinationPostVm, UriComponentsBuilder uriComponentsBuilder) {
        ProductOptionCombination productOptionCombination = new ProductOptionCombination();
        Product product = productRepository
                .findById(productOptionCombinationPostVm.productId())
                .orElseThrow(()-> new NotFoundException(String.format("Product %s is not found" ,productOptionCombinationPostVm.productId())));
        ProductOption productOption = productOptionRepository
                .findById(productOptionCombinationPostVm.productOptionId())
                .orElseThrow(()-> new NotFoundException(String.format("Product option %s is not found" , productOptionCombinationPostVm.productOptionId())));
        productOptionCombination.setProduct(product);
        productOptionCombination.setProductOption(productOption);
        productOptionCombination.setDisplayOrder(productOptionCombinationPostVm.displayOrder());
        productOptionCombination.setValue(productOptionCombinationPostVm.value());
        ProductOptionCombination savedProductOptionCombination = productOptioncombinationRepository.saveAndFlush(productOptionCombination);
        ProductOptionCombinationGetVm productOptionCombinationGetVm = ProductOptionCombinationGetVm.fromModel(savedProductOptionCombination);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/product-option-combinations/{id}").buildAndExpand(savedProductOptionCombination.getId()).toUri())
                .body(productOptionCombinationGetVm);
    }
    @GetMapping({"/backoffice/product-option-variations/{productId}"})
    public ResponseEntity<List<ProductVariationGetVm>> listProductVariationsOfProduct(@PathVariable("productId") Long productId){
        Product product = productRepository
                .findById(productId)
                .orElseThrow(()-> new NotFoundException(String.format("Product %s is not found" ,productId)));
        List<ProductVariationGetVm> listProductVariationVm = productRepository
                .findProductsByParent(product).stream()
                .map(ProductVariationGetVm::fromModel)
                .toList();
        return ResponseEntity.ok(listProductVariationVm);
    }
}
