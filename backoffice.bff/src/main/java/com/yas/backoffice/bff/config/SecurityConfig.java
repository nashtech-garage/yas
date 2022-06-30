package com.yas.backoffice.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange()
                .pathMatchers("/health").permitAll()
                .anyExchange().authenticated().and()
                .oauth2Client()
                .and()
                .oauth2Login()
                .and()
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable();
        return http.build();
    }
}
