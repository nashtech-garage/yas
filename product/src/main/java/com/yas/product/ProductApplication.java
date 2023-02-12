package com.yas.product;

import com.yas.product.config.ServiceUrlConfig;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(ServiceUrlConfig.class)
public class ProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApplication.class, args);
	}

	@Bean
	public ZipkinSpanExporter zipkinSpanExporter() {
		return ZipkinSpanExporter.builder()
						.setEndpoint("http://tempo:9411/api/v2/spans")
						.build();
	}

}
