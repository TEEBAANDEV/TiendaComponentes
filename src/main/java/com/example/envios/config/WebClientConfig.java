package com.example.envios.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

@Bean(name = "reciboWebClient")
    public WebClient webClient(){

        return WebClient.builder()
                .baseUrl("http://localhost:9095/api/v1/recibo").build();
}
}
