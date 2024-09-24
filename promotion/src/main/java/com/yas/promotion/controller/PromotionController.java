package com.yas.promotion.controller;

import com.yas.promotion.service.PromotionService;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.PromotionListVm;
import com.yas.promotion.viewmodel.PromotionPostVm;
import com.yas.promotion.viewmodel.PromotionPutVm;
import com.yas.promotion.viewmodel.error.ErrorVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate
    ) {
        return ResponseEntity.ok(promotionService.getPromotions(
            pageNo, pageSize, promotionName, couponCode, startDate, endDate));
    }


    @PostMapping("/backoffice/promotions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
            description = "Created",
            content = @Content(schema = @Schema(implementation = PromotionDetailVm.class))
            ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))
            ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))
            )
    })
    public ResponseEntity<PromotionDetailVm> createPromotion(@Valid @RequestBody PromotionPostVm promotionPostVm) {
        PromotionDetailVm promotionDetailVm = promotionService.createPromotion(promotionPostVm);
        return new ResponseEntity<>(promotionDetailVm, HttpStatus.CREATED);
    }

    @PutMapping("/backoffice/promotions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "Updated",
            content = @Content(schema = @Schema(implementation = PromotionPutVm.class))
            ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))
            ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))
            )
    })
    public ResponseEntity<PromotionDetailVm> updatePromotion(@Valid @RequestBody PromotionPutVm promotionPutVm) {
        PromotionDetailVm promotionDetailVm = promotionService.updatePromotion(promotionPutVm);
        return new ResponseEntity<>(promotionDetailVm, HttpStatus.OK);
    }

    @DeleteMapping("/backoffice/promotions/{promotionId}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deleted"),
        @ApiResponse(responseCode = "400", description = "Can't delete promotion",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))
    })
    public ResponseEntity<Void> deletePromotion(@PathVariable("promotionId") Long promotionId) {
        promotionService.deletePromotion(promotionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/backoffice/promotions/{promotionId}")
    public ResponseEntity<PromotionDetailVm> getPromotion(@PathVariable("promotionId") Long promotionId) {
        PromotionDetailVm promotionDetailVm = promotionService.getPromotion(promotionId);
        return new ResponseEntity<>(promotionDetailVm, HttpStatus.OK);
    }
}
