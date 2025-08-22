package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${api.server.url}")
    private String serverUrl;

    @Value("${api.server.description}")
    private String serverDescription;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // Define the single server for the API
                .servers(List.of(new Server().url(serverUrl).description(serverDescription)))

                // Define API metadata (title, version, etc.)
                .info(new Info()
                        .title("Vehicle Management System API")
                        .version("v1.0")
                        .description("This API provides endpoints for managing vehicles and related resources.")
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                )

                // Define global tags for organizing endpoints
                .tags(List.of(
                        new Tag().name("Car Management").description("APIs for creating, retrieving, and managing cars."),
                        new Tag().name("Authentication").description("APIs for user login and registration."),
                        new Tag().name("Log Auditing").description("APIs for retrieving audit logs.")
                ))

                // Add a global security requirement
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))

                // Define the security scheme component
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                );
    }
}