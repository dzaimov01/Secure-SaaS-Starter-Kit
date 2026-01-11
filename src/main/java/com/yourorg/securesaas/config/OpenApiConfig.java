package com.yourorg.securesaas.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    SecurityScheme bearerScheme =
        new SecurityScheme()
            .name("bearerAuth")
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");

    SecurityScheme apiKeyScheme =
        new SecurityScheme()
            .name("apiKeyAuth")
            .type(SecurityScheme.Type.APIKEY)
            .in(SecurityScheme.In.HEADER)
            .name("X-API-Key");

    return new OpenAPI()
        .info(new Info().title("Secure SaaS Starter API").version("0.1.0"))
        .components(new Components().addSecuritySchemes("bearerAuth", bearerScheme)
            .addSecuritySchemes("apiKeyAuth", apiKeyScheme))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
  }
}
