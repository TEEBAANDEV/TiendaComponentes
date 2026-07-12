package com.example.ventas.config;


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

    @Value("${services.inventario.url:http://localhost:9091/api/v1/inventario}")
    private String inventarioServiceUrl;

    @Value("${services.usuario.url:http://localhost:9092/api/v1/auth}")
    private String usuarioServiceUrl;

    @Value("${services.carrito.url:http://localhost:9093/api/v1/carrito}")
    private String carritoServiceUrl;

    private HttpClient ventasHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .responseTimeout(Duration.ofSeconds(10))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));
    }

    @Bean(name = "productoWebClient")
    public WebClient productowebClient(){
        return WebClient.builder()
                .baseUrl(productoServiceUrl)
                .clientConnector(new ReactorClientHttpConnector(ventasHttpClient()))
                .build();
    }
    @Bean(name = "inventarioWebClient")
    public WebClient inventarioWebClient() {
        return WebClient.builder()
                .baseUrl(inventarioServiceUrl)
                .clientConnector(new ReactorClientHttpConnector(ventasHttpClient()))
                .build();
    }

    @Bean(name = "usuarioWebClient")
    public WebClient usuarioWebClient() {
        return WebClient.builder()
                .baseUrl(usuarioServiceUrl)
                .clientConnector(new ReactorClientHttpConnector(ventasHttpClient()))
                .build();
    }

    @Bean(name = "carritoWebClient")
    public WebClient carritoWebClient() {
        return WebClient.builder()
                .baseUrl(carritoServiceUrl)
                .clientConnector(new ReactorClientHttpConnector(ventasHttpClient()))
                .build();
    }
}
