package com.yas.pricing.service.impl

import com.yas.pricing.domain.dto.PriceDto
import com.yas.pricing.repository.PriceRepository
import com.yas.pricing.service.PriceService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class PriceServiceImpl (private val priceRepository: PriceRepository) : PriceService {

    override fun getTop10Price(): Flux<PriceDto> {
        return priceRepository.findTop10().map { PriceDto(it.productId, it.priceName, it.price) }
    }
}