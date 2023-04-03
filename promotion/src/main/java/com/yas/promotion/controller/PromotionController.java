package com.yas.promotion.controller;

import com.yas.promotion.service.PromotionService;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.PromotionListVm;
import com.yas.promotion.viewmodel.PromotionPostVm;
import com.yas.promotion.viewmodel.PromotionVm;
import com.yas.promotion.viewmodel.error.ErrorVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping("/backoffice/promotions")
    public ResponseEntity<PromotionListVm> listPromotions(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "") String promotionName,
            @RequestParam(defaultValue = "") String couponCode,
            @RequestParam(defaultValue = "#{T(java.time.ZonedDateTime).of(1970, 1, 1, 0, 0, 0, 0, T(java.time.ZoneId).systemDefault())}") ZonedDateTime startDate,
            @RequestParam(defaultValue = "#{T(java.time.ZonedDateTime).now(T(java.time.ZoneId).systemDefault())}") ZonedDateTime endDate
    ) {
        return ResponseEntity.ok(promotionService.getPromotions(pageNo, pageSize, promotionName, couponCode, startDate, endDate));
    }


    @PostMapping("/backoffice/promotions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = PromotionDetailVm.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<PromotionDetailVm> createPromotion(@Valid @RequestBody PromotionPostVm promotionPostVm) {
        PromotionDetailVm promotionDetailVm = promotionService.createPromotion(promotionPostVm);
        return new ResponseEntity<>(promotionDetailVm, HttpStatus.CREATED);
    }
}
