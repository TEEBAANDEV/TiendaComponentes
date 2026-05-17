package com.example.inv_cliente.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "productoWebClient")
    public WebClient webClient(){
        return WebClient.builder().baseUrl("http://localhost:9090/api/v1/productos").build();
    }

}
