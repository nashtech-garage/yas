package com.yas.product.controller;

import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.product.ProductVariationVm;
import com.yas.product.viewmodel.productoption.ProductOptionValueGetVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.yas.product.utils.Constants;

import jakarta.validation.Valid;
import java.util.*;

@RestController
public class ProductOptionValueController {
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    public ProductOptionValueController(ProductOptionValueRepository productOptionValueRepository , ProductRepository productRepository , ProductOptionRepository productOptionRepository){
        this.productOptionValueRepository = productOptionValueRepository;
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
    }

    @GetMapping({"/backoffice/product-option-values"})
    public ResponseEntity<List<ProductOptionValueGetVm>> listProductOptionValues(){
        List<ProductOptionValueGetVm> productOptionGetVms = productOptionValueRepository
                .findAll().stream()
                .map(ProductOptionValueGetVm::fromModel)
                .toList();
        return ResponseEntity.ok(productOptionGetVms);
    }

    @GetMapping({"/storefront/product-option-values/{productId}"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "OK" , content = @Content(schema = @Schema(implementation =  ProductOptionValueGetVm.class))),
            @ApiResponse(responseCode = "404" , description = "Not found" , content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    public ResponseEntity<List<ProductVariationVm>> listProductOptionValueOfProduct(@PathVariable("productId") Long productId){
        Product product = productRepository
                .findById(productId)
                .orElseThrow(()-> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, productId));
        List<ProductVariationVm> productVariations = productOptionValueRepository
                .findAllByProduct(product).stream()
                .map(ProductVariationVm::fromModel)
                .toList();
        return ResponseEntity.ok(productVariations);
    }
    @PostMapping("/backoffice/product-option-values")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema( implementation = ProductOptionValueGetVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))
    })
    public ResponseEntity<Void> createProductOptionValue(@Valid @RequestBody ProductOptionValuePostVm productOptionValuePostVm, UriComponentsBuilder uriComponentsBuilder) {

        List<ProductOptionValue> productOptionValues = new ArrayList<>();

            Product product = productRepository
            .findById(productOptionValuePostVm.ProductId())
            .orElseThrow(()-> new NotFoundException(Constants.ERROR_CODE.PRODUCT_NOT_FOUND, productOptionValuePostVm.ProductId()));
            ProductOption productOption = productOptionRepository
            .findById(productOptionValuePostVm.ProductOptionId())
            .orElseThrow(()-> new NotFoundException(Constants.ERROR_CODE.PRODUCT_OPTION_IS_NOT_FOUND, productOptionValuePostVm.ProductOptionId()));
            for(String value: productOptionValuePostVm.value()){
                ProductOptionValue productOptionValue = new ProductOptionValue();
                productOptionValue.setProduct(product);
                productOptionValue.setProductOption(productOption);
                productOptionValue.setDisplayType(productOptionValuePostVm.displayType());
                productOptionValue.setDisplayOrder(productOptionValuePostVm.displayOrder());
                productOptionValue.setValue(value);
                productOptionValues.add(productOptionValue);
        }
        productOptionValueRepository.saveAllAndFlush(productOptionValues);
        return ResponseEntity.ok().build();
    }
    // @PutMapping("/backoffice/product-option-values/{id}")
    // @ApiResponses(value = {
    //         @ApiResponse(responseCode = "204", description = "No content"),
    //         @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    //         @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    // })
    // public ResponseEntity<Void> updateProductOptionValue(@PathVariable Long id, @Valid @RequestBody ProductOptionValuePostVm productOptionValuePostVm){
    //     ProductOptionValue productOptionValue = productOptionValueRepository
    //             .findById(id)
    //             .orElseThrow(()-> new NotFoundException(String.format("Product option value %s is not found", id)));
    //     Product product = productRepository
    //             .findById(productOptionValuePostVm.ProductId())
    //             .orElseThrow(()-> new NotFoundException(String.format("Product %s is not found" , productOptionValuePostVm.ProductId())));
    //     ProductOption productOption = productOptionRepository
    //             .findById(productOptionValuePostVm.ProductOptionId())
    //             .orElseThrow(()-> new NotFoundException(String.format("Product option %s is not found" , productOptionValuePostVm.ProductOptionId())));
    //     List<ProductOptionValue> productOptionValues = new ArrayList<>();
    //     for(String value: productOptionValuePostVm.value()){
    //         productOptionValue.setProduct(product);
    //         productOptionValue.setProductOption(productOption);
    //         productOptionValue.setDisplayType(productOptionValuePostVm.displayType());
    //         productOptionValue.setDisplayOrder(productOptionValuePostVm.displayOrder());
    //         productOptionValue.setValue(value);
    //         productOptionValues.add(productOptionValue);
    //     }
    //     productOptionValueRepository.saveAndFlush(productOptionValue);
    //     return ResponseEntity.noContent().build();
    // }
}
