package org.scottishtecharmy.wishaw_java.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI ltcOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("League Tournament Centre (LTC) API")
                        .description("REST API documentation for the League Tournament Centre - Esports & Sports Tournament Management Platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("LTC Team")
                                .email("support@ltc.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .schemaRequirement("Bearer Authentication",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat("JWT")
                                .scheme("bearer"));
    }
}

