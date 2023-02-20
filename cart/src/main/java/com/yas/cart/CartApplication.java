package com.yas.cart;

import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.yas.cart.config.ServiceUrlConfig;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(ServiceUrlConfig.class)
public class CartApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartApplication.class, args);
	}

	@Bean
	public ZipkinSpanExporter zipkinSpanExporter () {
		return ZipkinSpanExporter.builder().setEndpoint("http://tempo:9411/api/v2/spans").build();
	}


}
