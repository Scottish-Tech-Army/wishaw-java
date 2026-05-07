package org.scottishtecharmy.wishaw_java.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wishaw YMCA Digital Badging API")
                        .version("1.0.0")
                        .description("Backend API for the Wishaw YMCA Esports Academy digital badging and progress tracking system. "
                                + "Use the **Authorize** button to enter a JWT token obtained from the /api/auth/login endpoint.")
                        .contact(new Contact()
                                .name("Scottish Tech Army")
                                .url("https://www.scottishtecharmy.org"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .bearerFormat("JWT")
                                        .scheme("bearer")
                                        .description("Enter the JWT token obtained from the login endpoint")))
                .tags(List.of(
                        new Tag().name("Authentication").description("Login and registration endpoints"),
                        new Tag().name("Users").description("User management endpoints"),
                        new Tag().name("Centres").description("YMCA centre management"),
                        new Tag().name("Modules").description("Game module management (e.g. Minecraft, Rocket League, Fortnite)"),
                        new Tag().name("Badges").description("Core badges and sub-badge/challenge management"),
                        new Tag().name("Game Groups").description("Manage game groups per centre"),
                        new Tag().name("Levels").description("Level threshold management"),
                        new Tag().name("Progress").description("XP tracking, sub-badge completion, and user profiles"),
                        new Tag().name("Leaderboard").description("Global and centre-based leaderboards"),
                        new Tag().name("Welcome").description("Public welcome endpoint")
                ));
    }
}
