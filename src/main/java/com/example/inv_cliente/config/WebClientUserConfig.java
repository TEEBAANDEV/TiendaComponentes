package com.example.inv_cliente.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientUserConfig {

    @Bean(name = "usuarioWebClient")
    public WebClient usuarioWebClient(){
        return WebClient.builder().baseUrl("http://localhost:9092/api/v1/users").build();
    }
}
