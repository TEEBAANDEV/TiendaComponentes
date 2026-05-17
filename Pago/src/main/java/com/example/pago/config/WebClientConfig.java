package com.example.pago.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean(name = "ventasWebClient")
    public WebClient webClient(){ return WebClient.builder()
            .baseUrl("http://localhost:9094/api/v1/Ventas").build();}

}
