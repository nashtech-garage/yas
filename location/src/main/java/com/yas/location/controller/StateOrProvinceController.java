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
@RequestMapping(Constants.ApiConstant.STATE_OR_PROVINCES_URL)
public class StateOrProvinceController {
    private final StateOrProvinceRepository stateOrProvinceRepository;

    private final StateOrProvinceService stateOrProvinceService;

    public StateOrProvinceController(StateOrProvinceRepository stateOrProvinceRepository, StateOrProvinceService stateOrProvinceService) {
        this.stateOrProvinceRepository = stateOrProvinceRepository;
        this.stateOrProvinceService = stateOrProvinceService;
    }

    /**
     * API  paging list of state or province by country id
     *
     * @param pageNo     The number of page
     * @param pageSize   The number of row on every page
     * @param countryId  The country Id which  state or province belong
     *
     * @return StateOrProvinceListGetVm   The list of StateOrProvince
     */
    @GetMapping({"/paging"})
    public ResponseEntity<StateOrProvinceListGetVm> getPageableStateOrProvinces(@RequestParam(value = "pageNo", defaultValue = Constants.PageableConstant.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                                         @RequestParam(value = "pageSize", defaultValue = Constants.PageableConstant.DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                                                         @RequestParam(value = "countryId", required = false) Long countryId   ) {

        return ResponseEntity.ok(stateOrProvinceService.getPageableStateOrProvinces(pageNo, pageSize, countryId));
    }

    @GetMapping({""})
    public ResponseEntity<List<StateOrProvinceVm>> listStateOrProvinces() {
        List<StateOrProvinceVm> stateOrProvinceVms = stateOrProvinceRepository.findAll().stream()
                .map(StateOrProvinceVm::fromModel)
                .toList();
        return ResponseEntity.ok(stateOrProvinceVms);
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_200, description = Constants.ApiConstant.OK, content = @Content(schema = @Schema(implementation = StateOrProvinceVm.class))),
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_404, description = Constants.ApiConstant.NOT_FOUND, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<StateOrProvinceVm> getStateOrProvince(@PathVariable("id") Long id) {
        StateOrProvince stateOrProvince = stateOrProvinceRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.COUNTRY_NOT_FOUND, id));
        return ResponseEntity.ok(StateOrProvinceVm.fromModel(stateOrProvince));
    }

    /**
     * API create state or province
     *
     * @param stateOrProvincePostVm  The state or province post Dto
     *
     * @return StateOrProvince
     */
    @PostMapping("")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_201, description = Constants.ApiConstant.CREATED, content = @Content(schema = @Schema(implementation = StateOrProvinceVm.class))),
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_400, description = Constants.ApiConstant.BAD_REQUEST, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<StateOrProvinceVm> createStateOrProvince(@Valid @RequestBody StateOrProvincePostVm stateOrProvincePostVm, UriComponentsBuilder uriComponentsBuilder) {
        StateOrProvince stateOrProvince = stateOrProvinceService.createStateOrProvince(stateOrProvincePostVm);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/state-or-provinces/{id}").buildAndExpand(stateOrProvince.getId()).toUri())
                .body(StateOrProvinceVm.fromModel(stateOrProvince));
    }

    /**
     * API update state or province
     *
     * @param stateOrProvincePostVm   The state or province post Dto
     * @param  id                     The id of stateOrProvince need to update
     *
     * @return
     */
    @PutMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_204, description = Constants.ApiConstant.NO_CONTENT, content = @Content()),
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_404, description = Constants.ApiConstant.NOT_FOUND, content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_400, description = Constants.ApiConstant.BAD_REQUEST, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateStateOrProvince(@PathVariable Long id, @Valid @RequestBody final StateOrProvincePostVm stateOrProvincePostVm) {
        stateOrProvinceService.updateStateOrProvince(stateOrProvincePostVm, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_204, description = Constants.ApiConstant.NO_CONTENT, content = @Content()),
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_404, description = Constants.ApiConstant.NOT_FOUND, content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_400, description = Constants.ApiConstant.BAD_REQUEST, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteStateOrProvince(@PathVariable long id){
        StateOrProvince stateOrProvince = stateOrProvinceRepository.findById(id).orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.COUNTRY_NOT_FOUND, id));

        stateOrProvinceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
