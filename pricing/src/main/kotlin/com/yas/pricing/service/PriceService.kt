package com.yas.pricing.service

import com.yas.pricing.domain.dto.PriceDto
import reactor.core.publisher.Flux

interface PriceService {
    fun getTop10Price(): Flux<PriceDto>
}