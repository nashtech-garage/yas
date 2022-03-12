package com.yas.gateway

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono


@SpringBootApplication
@EnableWebFluxSecurity
class GatewayApplication {

    @Bean
    fun reactiveJwtDecoder() = NimbusReactiveJwtDecoder {
        val claimsSet = it.jwtClaimsSet
        Mono.just(claimsSet)
    }

	@Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.authorizeExchange { exchanges -> exchanges.anyExchange().authenticated() }
            .oauth2ResourceServer().jwt()
        http.csrf().disable()
        return http.build()
    }

}

fun main(args: Array<String>) {
	runApplication<GatewayApplication>(*args)
}
