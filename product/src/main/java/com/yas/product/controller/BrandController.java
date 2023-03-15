package com.yas.product.controller;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.repository.BrandRepository;
import com.yas.product.viewmodel.brand.BrandPostVm;
import com.yas.product.viewmodel.brand.BrandVm;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.utils.Constants;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
public class BrandController {
  private final BrandRepository brandRepository;
  private static final Logger log = LoggerFactory.getLogger(BrandController.class);

  public BrandController(BrandRepository brandRepository) {
    this.brandRepository = brandRepository;
  }

  @GetMapping({"/backoffice/brands", "/storefront/brands"})
  public ResponseEntity<List<BrandVm>> listBrands() {
    log.info("[Test logging with trace] Got a request");
    List<BrandVm> brandVms = brandRepository.findAll().stream()
        .map(BrandVm::fromModel)
        .toList();
    return ResponseEntity.ok(brandVms);
  }

  @GetMapping("/backoffice/brands/{id}")
  @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = BrandVm.class))),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public ResponseEntity<BrandVm> getBrand(@PathVariable("id") Long id) {
    Brand brand = brandRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.BRAND_NOT_FOUND, id));
    return ResponseEntity.ok(BrandVm.fromModel(brand));
  }

  @PostMapping("/backoffice/brands")
  @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = BrandVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public ResponseEntity<BrandVm> createBrand(@Validated @RequestBody BrandPostVm brandPostVm, UriComponentsBuilder uriComponentsBuilder) {
    Brand brand = brandPostVm.toModel();
    brandRepository.save(brand);
    return ResponseEntity.created(uriComponentsBuilder.replacePath("/brands/{id}").buildAndExpand(brand.getId()).toUri())
            .body(BrandVm.fromModel(brand));
  }

  @PutMapping("/backoffice/brands/{id}")
  @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public ResponseEntity<Void> updateBrand(@PathVariable Long id, @Validated @RequestBody final BrandPostVm brandPostVm) {
    Brand brand = brandRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.BRAND_NOT_FOUND, id));
    brand.setSlug(brandPostVm.slug());
    brand.setName(brandPostVm.name());
    brandRepository.save(brand);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/backoffice/brands/{id}")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
          @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
          @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public ResponseEntity<Void> deleteBrand(@PathVariable long id){
    Brand brand = brandRepository.findById(id).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.BRAND_NOT_FOUND, id));
    if(!brand.getProducts().isEmpty()){
      throw new BadRequestException(Constants.ERROR_CODE.MAKE_SURE_BRAND_DONT_CONTAINS_ANY_PRODUCT);
    }
    brandRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
