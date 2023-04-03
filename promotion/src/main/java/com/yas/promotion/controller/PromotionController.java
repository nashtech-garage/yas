package com.yas.promotion.controller;

import com.yas.promotion.service.PromotionService;
import com.yas.promotion.viewmodel.PromotionDetailVm;
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

    @GetMapping({"/backoffice/promotions"})
    public ResponseEntity<List<PromotionVm>> listPromotions(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
            @RequestParam(value = "promotionName", defaultValue = "", required = false) String promotionName,
            @RequestParam(value = "couponCode", defaultValue = "", required = false) String couponCode,
            @RequestParam(value = "startDate", defaultValue = "#{new java.util.Date(1970-01-01)}", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime startDate,
            @RequestParam(value = "endDate",  defaultValue="#{new java.util.Date()}", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime endDate
    ){
        return null;
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
