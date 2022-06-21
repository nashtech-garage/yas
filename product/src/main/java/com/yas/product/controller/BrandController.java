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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class BrandController {
  private final BrandRepository brandRepository;

  public BrandController(BrandRepository brandRepository) {
    this.brandRepository = brandRepository;
  }

  @GetMapping("brands")
  public List<BrandVm> list() {
    return brandRepository.findAll().stream()
        .map(item -> BrandVm.fromModel(item))
        .collect(Collectors.toList());
  }

  @GetMapping("brands/{id}")
  @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = BrandVm.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public BrandVm getDetail(@PathVariable("id") Long id) {
    Brand brand = brandRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("Brand %s is not found", id)));
    return BrandVm.fromModel(brand);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/brands")
  @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = BrandVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public ResponseEntity<Object> createBrand(@Valid @RequestBody BrandPostVm brandPostVm) {
    Brand brand = brandPostVm.toModel();
    brandRepository.save(brand);
    return ResponseEntity.ok(BrandVm.fromModel(brand));
  }

  @PutMapping("brands/{id}")
  @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public ResponseEntity<Object> update(@PathVariable Long id, @Valid @RequestBody final BrandPostVm brandPostVm) {
    Brand brand = brandRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("Brand %s is not found", id)));
    brand.setSlug(brandPostVm.slug());
    brand.setName(brandPostVm.name());
    brandRepository.save(brand);
    return ResponseEntity.noContent().build();
  }
}
