package org.scottishtecharmy.wishaw_java.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI wishawOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wishaw YMCA eSports API")
                        .version("v1")
                        .description("Enterprise-ready API for the Wishaw YMCA eSports Badge Portal. Supports both /api and /api/v1 route prefixes for frontend compatibility.")
                        .contact(new Contact().name("Scottish Tech Army")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local backend"),
                        new Server().url("/").description("Relative current host")
                ))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
