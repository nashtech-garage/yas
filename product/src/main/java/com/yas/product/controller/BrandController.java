package com.yas.product.controller;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.constants.PageableConstant;
import com.yas.product.model.Brand;
import com.yas.product.repository.BrandRepository;
import com.yas.product.service.BrandService;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.brand.BrandListGetVm;
import com.yas.product.viewmodel.brand.BrandPostVm;
import com.yas.product.viewmodel.brand.BrandVm;
import com.yas.product.viewmodel.error.ErrorVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class BrandController {
    private static final Logger log = LoggerFactory.getLogger(BrandController.class);
    private final BrandRepository brandRepository;
    private final BrandService brandService;

    public BrandController(BrandRepository brandRepository, BrandService brandService) {
        this.brandRepository = brandRepository;
        this.brandService = brandService;
    }

    @GetMapping({"/backoffice/brands", "/storefront/brands"})
    public ResponseEntity<List<BrandVm>> listBrands(
        @RequestParam(required = false, defaultValue = "") String brandName) {
        log.info("[Test logging with trace] Got a request");
        List<BrandVm> brandVms = brandRepository.findByNameContainingIgnoreCase(brandName).stream()
                .map(BrandVm::fromModel)
                .toList();
        return ResponseEntity.ok(brandVms);
    }

    @GetMapping({"/backoffice/brands/paging", "/storefront/brands/paging"})
    public ResponseEntity<BrandListGetVm> getPageableBrands(
            @RequestParam(value = "pageNo", defaultValue = PageableConstant.DEFAULT_PAGE_NUMBER, required = false)
            int pageNo,
            @RequestParam(value = "pageSize", defaultValue = PageableConstant.DEFAULT_PAGE_SIZE, required = false)
            int pageSize
    ) {

        return ResponseEntity.ok(brandService.getBrands(pageNo, pageSize));
    }

    @GetMapping("/backoffice/brands/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok",
            content = @Content(schema = @Schema(implementation = BrandVm.class))),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<BrandVm> getBrand(@PathVariable("id") Long id) {
        Brand brand = brandRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.BRAND_NOT_FOUND, id));
        return ResponseEntity.ok(BrandVm.fromModel(brand));
    }

    @PostMapping("/backoffice/brands")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created",
            content = @Content(schema = @Schema(implementation = BrandVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<BrandVm> createBrand(
            @Valid @RequestBody BrandPostVm brandPostVm,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        Brand brand = brandService.create(brandPostVm);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/brands/{id}")
            .buildAndExpand(brand.getId()).toUri())
                .body(BrandVm.fromModel(brand));
    }

    @PutMapping("/backoffice/brands/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateBrand(@PathVariable Long id, @Valid @RequestBody final BrandPostVm brandPostVm) {
        brandService.update(brandPostVm, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/backoffice/brands/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteBrand(@PathVariable long id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/backoffice/brands/by-ids")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<List<BrandVm>> getBrandsByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(brandService.getBrandsByIds(ids));
    }

}
