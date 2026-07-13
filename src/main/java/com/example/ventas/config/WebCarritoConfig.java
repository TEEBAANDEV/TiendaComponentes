package com.example.ventas.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebCarritoConfig {

    @Bean(name = "carritoWebClient")
    public WebClient webClient(){
        return WebClient.builder().baseUrl("http://localhost:9093/api/v1/carrito").build();
    }
}
