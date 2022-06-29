package com.yas.backoffice.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

//@Configuration
//public class SecurityConfig {
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http
//                .csrf(csrf -> csrf.disable())
//                .httpBasic().disable()
//                .formLogin().disable()
//                .authorizeExchange(exchanges -> exchanges
//                        .anyExchange().authenticated()
//                );
//
//
//        return http.build();
//    }
//}
