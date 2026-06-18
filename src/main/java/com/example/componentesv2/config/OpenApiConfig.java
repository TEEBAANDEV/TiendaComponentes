package com.example.componentesv2.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Producto Microservice API")
                        .version("1.0.0")
                        .description("API para la gestión de productos en la tienda de componentes, incluyendo navegación a través de HATEOAS."));
    }

    @Bean
    public GlobalOpenApiCustomizer removeLinksCustomizer() {
        return openApi -> {
            if (openApi.getComponents() != null && openApi.getComponents().getSchemas() != null) {
                openApi.getComponents().getSchemas().forEach((name, schema) -> {
                    if (schema.getProperties() != null) {
                        schema.getProperties().remove("links");
                        schema.getProperties().remove("_links");
                    }
                });
            }
        };
    }
}
