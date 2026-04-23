package com.yas.product.controller;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.constants.PageableConstant;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.service.ProductAttributeGroupService;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupListGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupPostVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
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
public class ProductAttributeGroupController {

    private final ProductAttributeGroupService productAttributeGroupService;
    private final ProductAttributeGroupRepository productAttributeGroupRepository;

    public ProductAttributeGroupController(ProductAttributeGroupRepository productAttributeGroupRepository,
                                           ProductAttributeGroupService productAttributeGroupService) {
        this.productAttributeGroupRepository = productAttributeGroupRepository;
        this.productAttributeGroupService = productAttributeGroupService;
    }

    @GetMapping("/backoffice/product-attribute-groups")
    public ResponseEntity<List<ProductAttributeGroupVm>> listProductAttributeGroups() {
        List<ProductAttributeGroupVm> productAttributeGroupVms = productAttributeGroupRepository.findAll().stream()
            .map(ProductAttributeGroupVm::fromModel)
            .toList();
        return ResponseEntity.ok(productAttributeGroupVms);
    }

    @GetMapping({"/backoffice/product-attribute-groups/paging", "/storefront/product-attribute-groups/paging"})
    public ResponseEntity<ProductAttributeGroupListGetVm> getPageableProductAttributeGroups(
        @RequestParam(value = "pageNo", defaultValue = PageableConstant.DEFAULT_PAGE_NUMBER, required = false)
        int pageNo,
        @RequestParam(value = "pageSize", defaultValue = PageableConstant.DEFAULT_PAGE_SIZE, required = false)
        int pageSize) {

        return ResponseEntity.ok(productAttributeGroupService.getPageableProductAttributeGroups(pageNo, pageSize));
    }

    @GetMapping("/backoffice/product-attribute-groups/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok",
            content = @Content(schema = @Schema(implementation = ProductAttributeGroupVm.class))),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
    })
    public ResponseEntity<ProductAttributeGroupVm> getProductAttributeGroup(@PathVariable("id") Long id) {
        ProductAttributeGroup productAttributeGroup = productAttributeGroupRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorCode.PRODUCT_ATTRIBUTE_GROUP_NOT_FOUND, id)));
        return ResponseEntity.ok(ProductAttributeGroupVm.fromModel(productAttributeGroup));
    }

    @PostMapping("/backoffice/product-attribute-groups")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created",
            content = @Content(schema = @Schema(implementation = ProductAttributeGroupVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<ProductAttributeGroupVm> createProductAttributeGroup(
        @Valid @RequestBody ProductAttributeGroupPostVm productAttributeGroupPostVm,
        UriComponentsBuilder uriComponentsBuilder
    ) {
        ProductAttributeGroup productAttributeGroup = productAttributeGroupPostVm.toModel();
        productAttributeGroupService.save(productAttributeGroup);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/product-attribute-groups/{id}")
            .buildAndExpand(productAttributeGroup.getId()).toUri())
                .body(ProductAttributeGroupVm.fromModel(productAttributeGroup));
    }

    @PutMapping("/backoffice/product-attribute-groups/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateProductAttributeGroup(
        @PathVariable Long id,
        @Valid @RequestBody final ProductAttributeGroupPostVm productAttributeGroupPostVm
    ) {
        ProductAttributeGroup productAttributeGroup = productAttributeGroupRepository
                .findById(id)
                .orElseThrow(
                    () -> new NotFoundException(
                        String.format(Constants.ErrorCode.PRODUCT_ATTRIBUTE_GROUP_NOT_FOUND, id)));
        productAttributeGroup.setName(productAttributeGroupPostVm.name());
        productAttributeGroupService.save(productAttributeGroup);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/backoffice/product-attribute-groups/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteProductAttributeGroup(@PathVariable Long id) {
        productAttributeGroupRepository.findById(id);
        try {
            productAttributeGroupRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(
                    Constants.ErrorCode.MAKE_SURE_PRODUCT_ATTRIBUTE_GROUP_DO_NOT_CONTAINS_ANY_PRODUCT_ATTRIBUTE);
        }
        return ResponseEntity.noContent().build();
    }
}
