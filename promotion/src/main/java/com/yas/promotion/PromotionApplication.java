package com.yas.promotion;

import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PromotionApplication {

	public static void main(String[] args) {
		SpringApplication.run(PromotionApplication.class, args);
	}

	@Bean
	public ZipkinSpanExporter zipkinSpanExporter() {
		return ZipkinSpanExporter.builder()
				.setEndpoint("http://tempo:9411/api/v2/spans")
				.build();
	}
}
