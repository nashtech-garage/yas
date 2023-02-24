package com.yas.rating.controller;

import com.yas.rating.service.RatingService;
import com.yas.rating.viewmodel.RatingListVm;
import com.yas.rating.viewmodel.RatingPostVm;
import com.yas.rating.viewmodel.RatingVm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }
    @GetMapping("/storefront/ratings/products/{productId}")
    public ResponseEntity<RatingListVm> getRatingList(
            @PathVariable Long productId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        return ResponseEntity.ok(ratingService.getRatingListByProductId(productId, pageNo, pageSize));
    }

    @PostMapping("/storefront/ratings")
    public ResponseEntity<RatingVm> createRating(RatingPostVm ratingPostVm) {
        return ResponseEntity.ok(ratingService.createRating(ratingPostVm));
    }
}
