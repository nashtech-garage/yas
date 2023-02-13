package com.yas.storefrontbff;

import com.yas.storefrontbff.config.ServiceUrlConfig;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication
@EnableWebFluxSecurity
@EnableConfigurationProperties(ServiceUrlConfig.class)
public class StorefrontBffApplication {

	public static void main(String[] args) {
		SpringApplication.run(StorefrontBffApplication.class, args);
	}

	//TODO move the zipkin endpoint to config file. Consider using Autoconfigure when it ready (alpha at the moment)
	@Bean
	public ZipkinSpanExporter zipkinSpanExporter() {
		return ZipkinSpanExporter.builder()
				.setEndpoint("http://tempo:9411/api/v2/spans")
				.build();
	}

}
