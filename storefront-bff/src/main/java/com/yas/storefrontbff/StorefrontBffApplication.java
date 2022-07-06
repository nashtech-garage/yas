package com.yas.storefrontbff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication
@EnableWebFluxSecurity
public class StorefrontBffApplication {

	public static void main(String[] args) {
		SpringApplication.run(StorefrontBffApplication.class, args);
	}

}
