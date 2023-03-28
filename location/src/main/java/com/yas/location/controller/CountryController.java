package com.yas.location.controller;

import com.yas.location.exception.BadRequestException;
import com.yas.location.exception.NotFoundException;
import com.yas.location.model.Country;
import com.yas.location.service.CountryService;
import com.yas.location.repository.CountryRepository;
import com.yas.location.viewmodel.country.CountryPostVm;
import com.yas.location.viewmodel.country.CountryVm;
import com.yas.location.viewmodel.error.ErrorVm;
import com.yas.location.viewmodel.country.CountryListGetVm;
import com.yas.location.utils.Constants;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(Constants.ApiConstant.COUNTRIES_URL)
public class CountryController {
    private final CountryRepository countryRepository;

    private final CountryService countryService;

    public CountryController(CountryRepository countryRepository, CountryService countryService) {
        this.countryRepository = countryRepository;
        this.countryService = countryService;
    }

    @GetMapping({"/paging"})
    public ResponseEntity<CountryListGetVm> getPageableCountries(@RequestParam(value = "pageNo", defaultValue = Constants.PageableConstant.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                 @RequestParam(value = "pageSize", defaultValue = Constants.PageableConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize) {

        return ResponseEntity.ok(countryService.getPageableCountries(pageNo, pageSize));
    }

    @GetMapping({""})
    public ResponseEntity<List<CountryVm>> listCountries() {
        List<CountryVm> countryVms = countryRepository.findAll().stream()
                .map(CountryVm::fromModel)
                .toList();
        return ResponseEntity.ok(countryVms);
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_200, description = Constants.ApiConstant.OK, content = @Content(schema = @Schema(implementation = CountryVm.class))),
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_404, description = Constants.ApiConstant.NOT_FOUND, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CountryVm> getCountry(@PathVariable("id") Long id) {
        Country country = countryRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.COUNTRY_NOT_FOUND, id));
        return ResponseEntity.ok(CountryVm.fromModel(country));
    }

    @PostMapping("")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_201, description = Constants.ApiConstant.CREATED, content = @Content(schema = @Schema(implementation = CountryVm.class))),
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_400, description = Constants.ApiConstant.BAD_REQUEST, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CountryVm> createCountry(@Valid @RequestBody CountryPostVm countryPostVm, UriComponentsBuilder uriComponentsBuilder) {
        Country country = countryService.create(countryPostVm);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/countries/{id}").buildAndExpand(country.getId()).toUri())
                .body(CountryVm.fromModel(country));
    }

    @PutMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_204, description = Constants.ApiConstant.NO_CONTENT, content = @Content()),
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_404, description = Constants.ApiConstant.NOT_FOUND, content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_400, description = Constants.ApiConstant.BAD_REQUEST, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateCountry(@PathVariable Long id, @Valid @RequestBody final CountryPostVm countryPostVm) {
        countryService.update(countryPostVm, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_204, description = Constants.ApiConstant.NO_CONTENT, content = @Content()),
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_404, description = Constants.ApiConstant.NOT_FOUND, content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_400, description = Constants.ApiConstant.BAD_REQUEST, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteCountry(@PathVariable long id){
        Country country = countryRepository.findById(id).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.COUNTRY_NOT_FOUND, id));

        countryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


