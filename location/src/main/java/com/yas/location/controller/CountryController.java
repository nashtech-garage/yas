package com.yas.location.controller;

import com.yas.location.model.Country;
import com.yas.location.service.CountryService;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.country.CountryListGetVm;
import com.yas.location.viewmodel.country.CountryPostVm;
import com.yas.location.viewmodel.country.CountryVm;
import com.yas.location.viewmodel.error.ErrorVm;
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
@RequestMapping(Constants.ApiConstant.COUNTRIES_URL)
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/paging")
    public ResponseEntity<CountryListGetVm> getPageableCountries(
        @RequestParam(value = "pageNo", defaultValue = Constants.PageableConstant.DEFAULT_PAGE_NUMBER, required = false)
        final int pageNo,
        @RequestParam(value = "pageSize", defaultValue = Constants.PageableConstant.DEFAULT_PAGE_SIZE, required = false)
        final int pageSize) {
        return ResponseEntity.ok(countryService.getPageableCountries(pageNo, pageSize));
    }

    @GetMapping
    public ResponseEntity<List<CountryVm>> listCountries() {
        return ResponseEntity.ok(countryService.findAllCountries());
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = Constants.ApiConstant.CODE_200, description = Constants.ApiConstant.OK,
            content = @Content(schema = @Schema(implementation = CountryVm.class))),
        @ApiResponse(responseCode = Constants.ApiConstant.CODE_404, description = Constants.ApiConstant.NOT_FOUND,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CountryVm> getCountry(@PathVariable("id") final Long id) {
        return ResponseEntity.ok(countryService.findById(id));
    }

    @PostMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = Constants.ApiConstant.CODE_201, description = Constants.ApiConstant.CREATED,
            content = @Content(schema = @Schema(implementation = CountryVm.class))),
        @ApiResponse(responseCode = Constants.ApiConstant.CODE_400, description = Constants.ApiConstant.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CountryVm> createCountry(
        @Valid @RequestBody final CountryPostVm countryPostVm,
        final UriComponentsBuilder uriComponentsBuilder) {
        final Country country = countryService.create(countryPostVm);
        return ResponseEntity.created(
                uriComponentsBuilder
                    .replacePath("/countries/{id}")
                    .buildAndExpand(country.getId())
                    .toUri())
            .body(CountryVm.fromModel(country));
    }

    @PutMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = Constants.ApiConstant.CODE_204, description = Constants.ApiConstant.NO_CONTENT,
            content = @Content()),
        @ApiResponse(responseCode = Constants.ApiConstant.CODE_404, description = Constants.ApiConstant.NOT_FOUND,
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = Constants.ApiConstant.CODE_400, description = Constants.ApiConstant.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateCountry(@PathVariable final Long id,
                                              @Valid @RequestBody final CountryPostVm countryPostVm) {
        countryService.update(countryPostVm, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = Constants.ApiConstant.CODE_204, description = Constants.ApiConstant.NO_CONTENT,
            content = @Content()),
        @ApiResponse(responseCode = Constants.ApiConstant.CODE_404, description = Constants.ApiConstant.NOT_FOUND,
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = Constants.ApiConstant.CODE_400, description = Constants.ApiConstant.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteCountry(@PathVariable(name = "id") final Long id) {
        countryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
