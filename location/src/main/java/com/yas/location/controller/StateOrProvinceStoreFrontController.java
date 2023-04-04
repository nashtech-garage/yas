package com.yas.location.controller;

import com.yas.location.service.StateOrProvinceService;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.error.ErrorVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.ApiConstant.STATE_OR_PROVINCES_STOREFRONT_URL)
public class StateOrProvinceStoreFrontController {
    private final StateOrProvinceService stateOrProvinceService;

    @GetMapping("/{countryId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_200, description = Constants.ApiConstant.OK, content = @Content(schema = @Schema(implementation = StateOrProvinceVm.class))),
            @ApiResponse(responseCode = Constants.ApiConstant.CODE_404, description = Constants.ApiConstant.NOT_FOUND, content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<List<StateOrProvinceVm>> getStateOrProvince(@PathVariable("countryId") final Long id) {
        return ResponseEntity.ok(stateOrProvinceService.getAllByCountryId(id));
    }

}
