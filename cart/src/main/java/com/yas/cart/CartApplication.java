package com.yas.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.yas.cart.config.ServiceUrlConfig;

@SpringBootApplication
@EnableConfigurationProperties(ServiceUrlConfig.class)
public class CartApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartApplication.class, args);
	}

}
