package com.yas.product.controller;

import java.util.List;
import java.util.stream.Collectors;
import com.yas.product.exception.NotFoundException;
import com.yas.product.mapper.BrandMapper;
import com.yas.product.model.Brand;
import com.yas.product.repository.BrandRepository;
import com.yas.product.viewModel.BrandDto;
import com.yas.product.viewModel.BrandPostDto;
import com.yas.product.viewModel.ErrorVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing {@link com.yas.product.model.Brand}.
 *
 * @author toaitrano
 * @version 1.0
 * @since 2022/06/18
 */
@RestController
@RequiredArgsConstructor
public class BrandController {

  private final BrandRepository brandRepository;
  private final BrandMapper brandMapper;

  @GetMapping("brands")
  public List<BrandDto> list() {
    return brandRepository.findAll().stream()
        .map(item -> new BrandDto(item.getId(), item.getName(), item.getSlug()))
        .collect(Collectors.toList());
  }

  @GetMapping("brands/{id}")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ok",
            content = @Content(schema = @Schema(implementation = BrandDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))
      })
  public BrandDto getDetail(@PathVariable("id") Long id) {
    Brand brand =
        brandRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("Brand %s is not found", id)));
    return new BrandDto(brand.getId(), brand.getName(), brand.getSlug());
  }

  @PostMapping("/brands")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Ok",
            content = @Content(schema = @Schema(implementation = BrandDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))
      })
  public BrandDto createBrand(@RequestBody BrandPostDto dto) {
    Brand brand = brandMapper.toEntity(dto);
    brandRepository.save(brand);
    return brandMapper.toDto(brand);
  }

  @PutMapping("brands/{id}")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))
      })
  public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody final BrandPostDto dto) {
    Brand brand =
        brandRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("Brand %s is not found", id)));
    brandMapper.updateEntity(brand, dto);
    brandRepository.save(brand);
    return ResponseEntity.noContent().build();
  }
}
