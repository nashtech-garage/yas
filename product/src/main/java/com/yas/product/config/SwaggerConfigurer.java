package com.yas.product.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "test"})
public class SwaggerConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerConfigurer.class);

    @Bean
    public OpenAPI getProductServiceOpenAPI() {
        LOGGER.info("Configuring Swagger for Product Service");
        return new OpenAPI()
                .info(new Info().title("Product Service API")
                        .description("Product API documentation application")
                        .version("v1"))
                .externalDocs(new ExternalDocumentation()
                        .description("Nashtech Yas API Documentation")
                        .url("https://github.com/nashtech-garage/yas"));
    }
}
