package com.yas.product.controller;

import com.yas.product.service.ProductService;
import com.yas.product.viewmodel.ErrorVm;
import com.yas.product.viewmodel.ProductGetDetailVm;
import com.yas.product.viewmodel.ProductPostVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping(path = "/products", consumes =  { MediaType.MULTIPART_FORM_DATA_VALUE })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = ProductGetDetailVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<ProductGetDetailVm> createProduct(@Valid @ModelAttribute ProductPostVm productPostVm, UriComponentsBuilder uriComponentsBuilder) {
        ProductGetDetailVm productGetDetailVm = productService.createProduct(productPostVm);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/products/{id}").buildAndExpand(productGetDetailVm.id()).toUri())
                .body(productGetDetailVm);
    }
}
