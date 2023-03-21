package com.yas.product.controller;

import com.yas.product.exception.NotFoundException;
import com.yas.product.model.ProductOption;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.service.ProductOptionService;
import com.yas.product.viewmodel.productoption.ProductOptionListGetVm;
import com.yas.product.constants.PageableConstant;
import com.yas.product.viewmodel.productattribute.ProductAttributeGetVm;
import com.yas.product.viewmodel.productoption.ProductOptionGetVm;
import com.yas.product.viewmodel.productoption.ProductOptionPostVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.List;

@RestController
public class ProductOptionController {
    private final ProductOptionRepository productOptionRepository;

    private final ProductOptionService productOptionService;
    public ProductOptionController(ProductOptionRepository productOptionRepository, ProductOptionService productOptionService) {
        this.productOptionRepository = productOptionRepository;
        this.productOptionService = productOptionService;
    }

    @GetMapping({"/backoffice/product-options"})
    public ResponseEntity<List<ProductOptionGetVm>> listProductOption() {
        List<ProductOptionGetVm> productOptionGetVms = productOptionRepository
                .findAll().stream()
                .map(ProductOptionGetVm::fromModel)
                .toList();
        return ResponseEntity.ok(productOptionGetVms);
    }

    @GetMapping({"/backoffice/product-options/paging", "/backoffice/product-options/paging"})
    public ResponseEntity<ProductOptionListGetVm> getPageableProductOptions(  @RequestParam(value = "pageNo", defaultValue = PageableConstant.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                                              @RequestParam(value = "pageSize", defaultValue = PageableConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize) {

        return ResponseEntity.ok(productOptionService.getPageableProductOptions(pageNo, pageSize));
    }

    @GetMapping("/backoffice/product-options/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ProductAttributeGetVm.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    public ResponseEntity<ProductOptionGetVm> getProductOption(@PathVariable("id") Long id) {
        ProductOption productOption = productOptionRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(Constants.ERROR_CODE.PRODUCT_OPTION_NOT_FOUND, id)));
        return ResponseEntity.ok(ProductOptionGetVm.fromModel(productOption));
    }

    @PostMapping("/backoffice/product-options")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = ProductOptionGetVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))
    })
    public ResponseEntity<ProductOptionGetVm> createProductOption(@Valid @RequestBody ProductOptionPostVm productOptionPostVm, Principal principal, UriComponentsBuilder uriComponentsBuilder) {
        ProductOption savedProductOption = productOptionService.create(productOptionPostVm);
        ProductOptionGetVm productOptionGetVm = ProductOptionGetVm.fromModel(savedProductOption);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/product-options/{id}").buildAndExpand(savedProductOption.getId()).toUri())
                .body(productOptionGetVm);
    }

    @PutMapping("/backoffice/product-options/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    public ResponseEntity<Void> updateProductOption(@PathVariable Long id, @Valid @RequestBody ProductOptionPostVm productOptionPostVm, Principal principal) {
        productOptionService.update(productOptionPostVm, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/backoffice/product-options/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteProductOption(@PathVariable Long id) {
        productOptionRepository.findById(id);
        productOptionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
