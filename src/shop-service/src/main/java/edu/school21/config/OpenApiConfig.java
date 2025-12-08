package edu.school21.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    private static final String TITLE = "Shop";
    private static final String VERSION = "1.0";
    private static final String DESCRIPTION = "Shop Microservices";
    private static final String SECURITY_SCHEMA = "Bearer";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("https://www.shop.ru")).info(getInfo())
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEMA))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEMA,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEMA)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));

    }

    private Info getInfo() {
        return new Info()
                .title(TITLE)
                .version(VERSION)
                .description(DESCRIPTION);
    }
}