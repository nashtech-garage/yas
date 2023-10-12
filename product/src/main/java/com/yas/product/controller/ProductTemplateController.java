package com.yas.product.controller;

import com.yas.product.constants.PageableConstant;
import com.yas.product.repository.ProductTemplateRepository;
import com.yas.product.service.ProductTemplateService;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeGetVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplateGetVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplateListGetVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplatePostVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplateVm;
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
public class ProductTemplateController {
    private final ProductTemplateService productTemplateService;
    private final ProductTemplateRepository productTemplateRepository;

    public ProductTemplateController(ProductTemplateService productTemplateService, ProductTemplateRepository productTemplateRepository) {
        this.productTemplateService = productTemplateService;
        this.productTemplateRepository = productTemplateRepository;
    }

    @GetMapping("/backoffice/product-template")
    public ResponseEntity<List<ProductTemplateGetVm>> listProductTemplate(){
        List<ProductTemplateGetVm> productTemplateVms = productTemplateRepository
                .findAll()
                .stream()
                .map(ProductTemplateGetVm::fromModel)
                .toList();
        return ResponseEntity.ok(productTemplateVms);
    }
    @GetMapping("/backoffice/product-template/paging")
    public ResponseEntity<ProductTemplateListGetVm> getPageableProductTemplates(@RequestParam(value = "pageNo", defaultValue = PageableConstant.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                                @RequestParam(value = "pageSize", defaultValue = PageableConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize) {

        return ResponseEntity.ok(productTemplateService.getPageableProductTemplate(pageNo, pageSize));
    }

    @GetMapping("/backoffice/product-template/{id}" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "OK" , content = @Content(schema = @Schema(implementation =  ProductAttributeGetVm.class))),
            @ApiResponse(responseCode = "404" , description = "Not found" , content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    public ResponseEntity<ProductTemplateVm> getProductTemplate(@PathVariable("id") Long id){
        return ResponseEntity.ok(productTemplateService.getProductTemplate(id));
    }

    @PostMapping("/backoffice/product-template")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = ProductAttributeGetVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<ProductTemplateVm> createProductTemplate(@Valid @RequestBody ProductTemplatePostVm productTemplatePostVm, UriComponentsBuilder uriComponentsBuilder, Principal principal) {
        ProductTemplateVm saveProductTemplate = productTemplateService.saveProductTemplate(productTemplatePostVm);
        return  ResponseEntity.created(uriComponentsBuilder.replacePath("/product-template/{id}").buildAndExpand(saveProductTemplate.id()).toUri())
                .body(saveProductTemplate);
    }


}