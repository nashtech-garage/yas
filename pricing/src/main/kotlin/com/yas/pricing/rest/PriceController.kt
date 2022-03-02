package com.yas.pricing.rest

import com.yas.pricing.service.PriceService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController (private val priceService: PriceService) {

    @GetMapping("/prices/10")
    fun getTopPrice() = priceService.getTop10Price()
}
