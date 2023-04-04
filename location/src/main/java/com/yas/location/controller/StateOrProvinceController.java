package com.yas.location.controller;

import com.yas.location.model.StateOrProvince;
import com.yas.location.service.StateOrProvinceService;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.error.ErrorVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceListGetVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvincePostVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceAndCountryGetNameVm;
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
@RequestMapping(Constants.ApiConstant.STATE_OR_PROVINCES_URL)
public class StateOrProvinceController {

  private final StateOrProvinceService stateOrProvinceService;

  public StateOrProvinceController(StateOrProvinceService stateOrProvinceService) {
    this.stateOrProvinceService = stateOrProvinceService;
  }

  /**
   * API  paging list of state or province by country id
   *
   * @param pageNo    The number of page
   * @param pageSize  The number of row on every page
   * @param countryId The country Id which  state or province belong
   * @return StateOrProvinceListGetVm   The list of StateOrProvince
   */
  @GetMapping("/paging")
  public ResponseEntity<StateOrProvinceListGetVm> getPageableStateOrProvinces(
      @RequestParam(value = "pageNo", defaultValue = Constants.PageableConstant.DEFAULT_PAGE_NUMBER, required = false) final int pageNo,
      @RequestParam(value = "pageSize", defaultValue = Constants.PageableConstant.DEFAULT_PAGE_SIZE, required = false) final int pageSize,
      @RequestParam(value = "countryId", required = false) final Long countryId) {
    return ResponseEntity.ok(
        stateOrProvinceService.getPageableStateOrProvinces(pageNo, pageSize, countryId));
  }

  @GetMapping
  public ResponseEntity<List<StateOrProvinceVm>> getAllByCountryId( @RequestParam(value = "countryId", required = false) final Long countryId) {
    return ResponseEntity.ok(
            stateOrProvinceService.getAllByCountryId(countryId));
  }

  @GetMapping("/{id}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = Constants.ApiConstant.CODE_200, description = Constants.ApiConstant.OK, content = @Content(schema = @Schema(implementation = StateOrProvinceVm.class))),
      @ApiResponse(responseCode = Constants.ApiConstant.CODE_404, description = Constants.ApiConstant.NOT_FOUND, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public ResponseEntity<StateOrProvinceVm> getStateOrProvince(@PathVariable("id") final Long id) {
    return ResponseEntity.ok(stateOrProvinceService.findById(id));
  }

  /**
   * API Get list names for state and country by list of state or province ids
   *
   * @param   stateOrProvinceIds    The list of state or province ids
   * @return  StateOrProvinceAndCountryGetNameVm   The list of state and country names
   */
  @GetMapping("state-country-names")
  @ApiResponses(value = {
          @ApiResponse(responseCode = Constants.ApiConstant.CODE_200, description = Constants.ApiConstant.OK, content = @Content(schema = @Schema(implementation = StateOrProvinceVm.class))),
          @ApiResponse(responseCode = Constants.ApiConstant.CODE_404, description = Constants.ApiConstant.NOT_FOUND, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public ResponseEntity<List<StateOrProvinceAndCountryGetNameVm>> getStateOrProvinceAndCountryNames(@RequestParam(value = "stateOrProvinceIds") final List<Long> stateOrProvinceIds) {
    return ResponseEntity.ok(stateOrProvinceService.getStateOrProvinceAndCountryNames(stateOrProvinceIds));
  }

  /**
   * API create state or province
   *
   * @param stateOrProvincePostVm The state or province post Dto
   * @return StateOrProvince
   */
  @PostMapping
  @ApiResponses(value = {
      @ApiResponse(responseCode = Constants.ApiConstant.CODE_201, description = Constants.ApiConstant.CREATED, content = @Content(schema = @Schema(implementation = StateOrProvinceVm.class))),
      @ApiResponse(responseCode = Constants.ApiConstant.CODE_400, description = Constants.ApiConstant.BAD_REQUEST, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public ResponseEntity<StateOrProvinceVm> createStateOrProvince(
      @Valid @RequestBody final StateOrProvincePostVm stateOrProvincePostVm,
      final UriComponentsBuilder uriComponentsBuilder) {
    final StateOrProvince stateOrProvince = stateOrProvinceService.createStateOrProvince(
        stateOrProvincePostVm);
    return ResponseEntity.created(uriComponentsBuilder.replacePath("/state-or-provinces/{id}")
            .buildAndExpand(stateOrProvince.getId()).toUri())
        .body(StateOrProvinceVm.fromModel(stateOrProvince));
  }

  /**
   * API update state or province
   *
   * @param stateOrProvincePostVm The state or province post Dto
   * @param id                    The id of stateOrProvince need to update
   * @return
   */
  @PutMapping("/{id}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = Constants.ApiConstant.CODE_204, description = Constants.ApiConstant.NO_CONTENT, content = @Content()),
      @ApiResponse(responseCode = Constants.ApiConstant.CODE_404, description = Constants.ApiConstant.NOT_FOUND, content = @Content(schema = @Schema(implementation = ErrorVm.class))),
      @ApiResponse(responseCode = Constants.ApiConstant.CODE_400, description = Constants.ApiConstant.BAD_REQUEST, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public ResponseEntity<Void> updateStateOrProvince(@PathVariable final Long id,
      @Valid @RequestBody final StateOrProvincePostVm stateOrProvincePostVm) {
    stateOrProvinceService.updateStateOrProvince(stateOrProvincePostVm, id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = Constants.ApiConstant.CODE_204, description = Constants.ApiConstant.NO_CONTENT, content = @Content()),
      @ApiResponse(responseCode = Constants.ApiConstant.CODE_404, description = Constants.ApiConstant.NOT_FOUND, content = @Content(schema = @Schema(implementation = ErrorVm.class))),
      @ApiResponse(responseCode = Constants.ApiConstant.CODE_400, description = Constants.ApiConstant.BAD_REQUEST, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
  public ResponseEntity<Void> deleteStateOrProvince(@PathVariable final long id) {
    stateOrProvinceService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
