package com.yas.promotion.controller;

import com.yas.promotion.model.Promotion;
import com.yas.promotion.service.PromotionService;
import com.yas.promotion.viewmodel.PromotionPostVm;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.error.ErrorVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;


    @PostMapping("/backoffice/promotions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = PromotionDetailVm.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<PromotionDetailVm> createPromotion(@Valid @RequestBody PromotionPostVm promotionPostVm, UriComponentsBuilder uriComponentsBuilder){
        Promotion savedPromotion = promotionService.create(promotionPostVm);

        PromotionDetailVm promotionDetailVm = PromotionDetailVm.fromModel(savedPromotion);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/promotions/{id}")
                .buildAndExpand(savedPromotion.getId()).toUri()).body(promotionDetailVm);
    }
}
