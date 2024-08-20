package com.yas.tax.controller;

import com.yas.tax.constants.ApiConstant;
import com.yas.tax.constants.PageableConstant;
import com.yas.tax.model.TaxRate;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.error.ErrorVm;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(ApiConstant.TAX_RATE_URL)
public class TaxRateController {
    private final TaxRateService taxRateService;

    public TaxRateController(TaxRateService taxRateService) {
        this.taxRateService = taxRateService;
    }

    @GetMapping("/paging")
    public ResponseEntity<TaxRateListGetVm> getPageableTaxRates(
        @RequestParam(value = "pageNo", defaultValue = PageableConstant.DEFAULT_PAGE_NUMBER, required = false)
        final int pageNo,
        @RequestParam(value = "pageSize", defaultValue = PageableConstant.DEFAULT_PAGE_SIZE, required = false)
        final int pageSize) {
        return ResponseEntity.ok(taxRateService.getPageableTaxRates(pageNo, pageSize));
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ApiConstant.CODE_200, description = ApiConstant.OK,
            content = @Content(schema = @Schema(implementation = TaxRateVm.class))),
        @ApiResponse(responseCode = ApiConstant.CODE_404, description = ApiConstant.NOT_FOUND,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<TaxRateVm> getTaxRate(@PathVariable("id") final Long id) {
        return ResponseEntity.ok(taxRateService.findById(id));
    }

    @PostMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = ApiConstant.CODE_201, description = ApiConstant.CREATED,
            content = @Content(schema = @Schema(implementation = TaxRateVm.class))),
        @ApiResponse(responseCode = ApiConstant.CODE_400, description = ApiConstant.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<TaxRateVm> createTaxRate(
        @Valid @RequestBody final TaxRatePostVm taxRatePostVm,
        final UriComponentsBuilder uriComponentsBuilder) {
        final TaxRate taxRate = taxRateService.createTaxRate(taxRatePostVm);
        return ResponseEntity.created(
                uriComponentsBuilder
                    .replacePath("/tax-rates/{id}")
                    .buildAndExpand(taxRate.getId())
                    .toUri())
            .body(TaxRateVm.fromModel(taxRate));
    }

    @PutMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ApiConstant.CODE_204, description = ApiConstant.NO_CONTENT, content = @Content()),
        @ApiResponse(responseCode = ApiConstant.CODE_404, description = ApiConstant.NOT_FOUND,
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = ApiConstant.CODE_400, description = ApiConstant.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateTaxRate(@PathVariable final Long id,
                                              @Valid @RequestBody final TaxRatePostVm taxRatePostVm) {
        taxRateService.updateTaxRate(taxRatePostVm, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ApiConstant.CODE_204, description = ApiConstant.NO_CONTENT, content = @Content()),
        @ApiResponse(responseCode = ApiConstant.CODE_404, description = ApiConstant.NOT_FOUND,
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = ApiConstant.CODE_400, description = ApiConstant.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteTaxRate(@PathVariable(name = "id") final Long id) {
        taxRateService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tax-percent")
    public ResponseEntity<Double> getTaxPercentByAddress(
        @RequestParam(value = "taxClassId", required = true) final Long taxClassId,
        @RequestParam(value = "countryId", required = true) final Long countryId,
        @RequestParam(value = "stateOrProvinceId", required = false) final Long stateOrProvinceId,
        @RequestParam(value = "zipCode", required = false) final String zipCode) {
        return ResponseEntity.ok(taxRateService.getTaxPercent(taxClassId, countryId, stateOrProvinceId, zipCode));
    }

    @GetMapping("/location-rates")
    public ResponseEntity<List<TaxRateVm>> getBulkTaxPercentsByAddress(
        @RequestParam(value = "taxClassIds", required = true) final List<Long> taxClassIds,
        @RequestParam(value = "countryId", required = true) final Long countryId,
        @RequestParam(value = "stateOrProvinceId", required = false) final Long stateOrProvinceId,
        @RequestParam(value = "zipCode", required = false) final String zipCode) {
        return ResponseEntity.ok(taxRateService.getBulkTaxRate(taxClassIds, countryId, stateOrProvinceId, zipCode));
    }
}
