package com.yas.storefrontbff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
                .pathMatchers("/profile/**").authenticated()
                .anyExchange().permitAll()
                .and()
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
