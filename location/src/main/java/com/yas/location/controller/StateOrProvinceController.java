package com.yas.location.controller;

import com.yas.location.exception.BadRequestException;
import com.yas.location.exception.NotFoundException;
import com.yas.location.model.StateOrProvince;
import com.yas.location.service.StateOrProvinceService;
import com.yas.location.repository.StateOrProvinceRepository;
import com.yas.location.viewmodel.stateorprovince.StateOrProvincePostVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import com.yas.location.viewmodel.error.ErrorVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceListGetVm;
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
public class StateOrProvinceController {
    private final StateOrProvinceRepository stateOrProvinceRepository;

    private final StateOrProvinceService stateOrProvinceService;

    public StateOrProvinceController(StateOrProvinceRepository stateOrProvinceRepository, StateOrProvinceService stateOrProvinceService) {
        this.stateOrProvinceRepository = stateOrProvinceRepository;
        this.stateOrProvinceService = stateOrProvinceService;
    }

    @GetMapping({"/backoffice/state-or-provinces/paging"})
    public ResponseEntity<StateOrProvinceListGetVm> getPageableCountries(@RequestParam(value = "pageNo", defaultValue = Constants.PageableConstant.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                 @RequestParam(value = "pageSize", defaultValue = Constants.PageableConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                                                         @RequestParam(value = "countryId", required = false) Long countryId   ) {

        return ResponseEntity.ok(stateOrProvinceService.getPageableCountries(pageNo, pageSize, countryId));
    }

    @GetMapping({"/backoffice/state-or-provinces"})
    public ResponseEntity<List<StateOrProvinceVm>> listCountries() {
        List<StateOrProvinceVm> stateOrProvinceVms = stateOrProvinceRepository.findAll().stream()
                .map(StateOrProvinceVm::fromModel)
                .toList();
        return ResponseEntity.ok(stateOrProvinceVms);
    }

    @GetMapping("/backoffice/state-or-provinces/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = StateOrProvinceVm.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<StateOrProvinceVm> getStateOrProvince(@PathVariable("id") Long id) {
        StateOrProvince stateOrProvince = stateOrProvinceRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.COUNTRY_NOT_FOUND, id));
        return ResponseEntity.ok(StateOrProvinceVm.fromModel(stateOrProvince));
    }

    @PostMapping("/backoffice/state-or-provinces")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = StateOrProvinceVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<StateOrProvinceVm> createStateOrProvince(@Valid @RequestBody StateOrProvincePostVm stateOrProvincePostVm, UriComponentsBuilder uriComponentsBuilder) {
        StateOrProvince stateOrProvince = stateOrProvinceService.create(stateOrProvincePostVm);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/state-or-provinces/{id}").buildAndExpand(stateOrProvince.getId()).toUri())
                .body(StateOrProvinceVm.fromModel(stateOrProvince));
    }

    @PutMapping("/backoffice/state-or-provinces/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateStateOrProvince(@PathVariable Long id, @Valid @RequestBody final StateOrProvincePostVm stateOrProvincePostVm) {
        stateOrProvinceService.update(stateOrProvincePostVm, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/backoffice/state-or-provinces/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteStateOrProvince(@PathVariable long id){
        StateOrProvince stateOrProvince = stateOrProvinceRepository.findById(id).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.COUNTRY_NOT_FOUND, id));

        stateOrProvinceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

