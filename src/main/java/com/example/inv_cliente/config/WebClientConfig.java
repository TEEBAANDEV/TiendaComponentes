package com.example.inv_cliente.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration

public class WebClientConfig {
    @Value("${services.producto.url:http://localhost:9090/api/v1/productos}")
    private String productoServiceUrl;

    @Value("${services.inventario.url:http://localhost:9091/api/v1/inventario/producto}")
    private String inventarioServiceUrl;


    private HttpClient webClient () {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .responseTimeout(Duration.ofSeconds(10))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));

    }


    @Bean(name = "productoWebClient")
    public WebClient productoWebClient() {
        return WebClient.builder()
                .baseUrl(productoServiceUrl)
                .clientConnector(new ReactorClientHttpConnector(webClient()))
                .build();
    }

    @Bean(name = "inventarioWebClient")
    public WebClient inventarioWebClient() {
        return WebClient.builder()
                .baseUrl(inventarioServiceUrl)
                .clientConnector(new ReactorClientHttpConnector(webClient()))
                .build();
    }
}
