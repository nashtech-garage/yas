package com.yas.location.controller;

import com.yas.location.exception.BadRequestException;
import com.yas.location.exception.NotFoundException;
import com.yas.location.model.Country;
import com.yas.location.service.CountryService;
import com.yas.location.repository.CountryRepository;
import com.yas.location.viewmodel.country.CountryPostVm;
import com.yas.location.viewmodel.country.CountryVm;
import com.yas.location.viewmodel.error.ErrorVm;
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
public class CountryController {
    private final CountryRepository countryRepository;

    private final CountryService countryService;

    public CountryController(CountryRepository countryRepository, CountryService countryService) {
        this.countryRepository = countryRepository;
        this.countryService = countryService;
    }

    @GetMapping({"/backoffice/countries"})
    public ResponseEntity<List<CountryVm>> listCountries() {
        List<CountryVm> countryVms = countryRepository.findAll().stream()
                .map(CountryVm::fromModel)
                .toList();
        return ResponseEntity.ok(countryVms);
    }

    @GetMapping("/backoffice/country/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = CountryVm.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CountryVm> getCountry(@PathVariable("id") Long id) {
        Country country = countryRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.COUNTRY_NOT_FOUND, id));
        return ResponseEntity.ok(CountryVm.fromModel(country));
    }

    @PostMapping("/backoffice/country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = CountryVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CountryVm> createCountry(@Valid @RequestBody CountryPostVm countryPostVm, UriComponentsBuilder uriComponentsBuilder) {
        Country country = countryService.create(countryPostVm);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/country/{id}").buildAndExpand(country.getId()).toUri())
                .body(CountryVm.fromModel(country));
    }

    @PutMapping("/backoffice/country/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateCountry(@PathVariable Long id, @Valid @RequestBody final CountryPostVm countryPostVm) {
        countryService.update(countryPostVm, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/backoffice/country/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteCountry(@PathVariable long id){
        Country country = countryRepository.findById(id).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.COUNTRY_NOT_FOUND, id));

        countryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


