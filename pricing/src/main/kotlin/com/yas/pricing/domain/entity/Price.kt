package com.yas.pricing.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column

data class Price(@Id val id: Int?, @Column("product_id") val productId: Long, @Column("price_name") val priceName: String, val price: Double)
