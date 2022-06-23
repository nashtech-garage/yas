package com.yas.product.controller;

import java.util.List;
import java.util.stream.Collectors;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.repository.BrandRepository;
import com.yas.product.viewmodel.BrandVm;
import com.yas.product.viewmodel.BrandPostVm;
import com.yas.product.viewmodel.ErrorVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
public class BrandController {
  private final BrandRepository brandRepository;

  public BrandController(BrandRepository brandRepository) {
    this.brandRepository = brandRepository;
  }

  @GetMapping("/brands")
  public ResponseEntity<List<BrandVm>> listBrands() {
    List<BrandVm> brandVms = brandRepository.findAll().stream()
        .map(item -> BrandVm.fromModel(item))
        .collect(Collectors.toList());
    return ResponseEntity.ok(brandVms);
  }

  @GetMapping("/brands/{id}")
  @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = BrandVm.class))),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public ResponseEntity<BrandVm> getBrand(@PathVariable("id") Long id) {
    Brand brand = brandRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("Brand %s is not found", id)));
    return ResponseEntity.ok(BrandVm.fromModel(brand));
  }

  @PostMapping("/brands")
  @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = BrandVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public ResponseEntity<BrandVm> createBrand(@Valid @RequestBody BrandPostVm brandPostVm, UriComponentsBuilder uriComponentsBuilder) {
    Brand brand = brandPostVm.toModel();
    brandRepository.save(brand);
    return ResponseEntity.created(uriComponentsBuilder.replacePath("/brands/{id}").buildAndExpand(brand.getId()).toUri())
            .body(BrandVm.fromModel(brand));
  }

  @PutMapping("/brands/{id}")
  @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public ResponseEntity<Void> updateBrand(@PathVariable Long id, @Valid @RequestBody final BrandPostVm brandPostVm) {
    Brand brand = brandRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("Brand %s is not found", id)));
    brand.setSlug(brandPostVm.slug());
    brand.setName(brandPostVm.name());
    brandRepository.save(brand);
    return ResponseEntity.noContent().build();
  }
}
