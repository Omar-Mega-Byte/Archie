package com.archie.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for API documentation
 */
@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI archieOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Archie - Blueprint to Boot API")
                                                .description("AI-powered Spring Boot project generator from hand-drawn diagrams. "
                                                                +
                                                                "Upload architectural diagrams and get functional Spring Boot code instantly. "
                                                                +
                                                                "Includes JWT-based authentication system.")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Archie Development Team")
                                                                .email("archie@example.com"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")))
                                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                                .components(new Components()
                                                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                                                .name("bearerAuth")
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")
                                                                .description("Enter your JWT token in the format: Bearer {token}")));
        }
}
