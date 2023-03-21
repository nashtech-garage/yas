package com.yas.rating.controller;

import com.yas.rating.service.RatingService;
import com.yas.rating.viewmodel.RatingListVm;
import com.yas.rating.viewmodel.RatingPostVm;
import com.yas.rating.viewmodel.RatingVm;
import com.yas.rating.viewmodel.ResponeStatusVm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/backoffice/ratings/products/{productId}")
    public ResponseEntity<RatingListVm> getRatingListWithFilter(
            @PathVariable Long productId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
            @RequestParam(value = "name", defaultValue = "", required = false) String name) {

        return ResponseEntity.ok(ratingService.getRatingListByProductIdAndCustomerName(productId, name, pageNo, pageSize));
    }

    @DeleteMapping("/backoffice/ratings/{id}")
    public ResponseEntity<ResponeStatusVm> deleteRating(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.deleteRating(id));
    }

    @GetMapping({"/storefront/ratings/products/{productId}"})
    public ResponseEntity<RatingListVm> getRatingList(
            @PathVariable Long productId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize) {

        return ResponseEntity.ok(ratingService.getRatingListByProductId(productId, pageNo, pageSize));
    }

    @PostMapping("/storefront/ratings")
    public ResponseEntity<RatingVm> createRating(@Valid @RequestBody RatingPostVm ratingPostVm) {
        return ResponseEntity.ok(ratingService.createRating(ratingPostVm));
    }

    @GetMapping("/storefront/ratings/product/{productId}/average-star")
    public Double getAverageStarOfProduct(@PathVariable Long productId) {
        return ratingService.calculateAverageStar(productId);
    }
}
