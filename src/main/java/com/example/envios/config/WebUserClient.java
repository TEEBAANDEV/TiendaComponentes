package com.example.envios.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebUserClient {

    @Bean(name = "userWebClient")
    public WebClient webClient(){
        return WebClient.builder().baseUrl("http://localhost:9092/api/v1/users").build();
    }
}
