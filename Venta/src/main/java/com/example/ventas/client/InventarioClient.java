package com.example.ventas.client;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class InventarioClient {

    private final WebClient webClient;

    public InventarioClient(@Qualifier("inventarioWebClient") WebClient webClient){
        this.webClient = webClient;
    }

    public Mono<Void> descontarStock(Long idProducto, int cantidad){
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/descontar")
                        .queryParam("idProducto", idProducto)
                        .queryParam("cantidad" , cantidad)
                        .build())
                .retrieve()
                .bodyToMono(Void.class);
    }
}
