package com.yas.recommendation.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(
                auth -> auth.requestMatchers(
                    "/actuator/prometheus",
                    "/actuator/health/**",
                    "/swagger-ui",
                    "/swagger-ui/**",
                    "/error",
                    "/v3/api-docs/**",
                    "/embedding/product/**"
                ).permitAll().anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .build();
    }
}
