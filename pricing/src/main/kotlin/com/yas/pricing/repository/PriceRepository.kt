package com.yas.pricing.repository

import com.yas.pricing.domain.entity.Price
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface PriceRepository : ReactiveCrudRepository<Price, Int> {
    @Query("SELECT * from Price order by id limit 10")
    fun findTop10() : Flux<Price>
}