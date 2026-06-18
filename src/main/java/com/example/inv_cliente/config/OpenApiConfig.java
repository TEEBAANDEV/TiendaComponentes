package com.example.inv_cliente.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Carrito (Inventario Cliente)")
                        .version("1.0")
                        .description("Microservicio para la gestión del carrito de compras de componentes de los clientes.")
                        .contact(new Contact().name("Soporte").email("soporte@example.com")));
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
