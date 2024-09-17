package com.yas.cart.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(title = "Product Service API", description = "Product API documentation",
    version = "1.0"), security = @SecurityRequirement(name = "oauth2_bearer"),
    servers = {@Server(url = "${server.servlet.context-path}", description = "Default Server URL")})
@SecurityScheme(name = "oauth2_bearer", type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(authorizationCode = @OAuthFlow(
        authorizationUrl = "${springdoc.oauthflow.authorization-url}",
        tokenUrl = "${springdoc.oauthflow.token-url}", scopes = {
            @OAuthScope(name = "openid", description = "openid")})))
public class SwaggerConfig {
}
