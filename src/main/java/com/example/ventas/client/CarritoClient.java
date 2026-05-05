package com.example.ventas.client;


import com.example.ventas.model.CarritoDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CarritoClient {

    private final WebClient webClient;

    public CarritoClient(@Qualifier("carritoWebClient") WebClient webClient){
        this.webClient = webClient;
    }

    public Mono<List<CarritoDTO>> obtenerCarritoPorUsuario(Long idUsuario){
        return webClient.get()
                .uri("/usuario/{idUsuario}",idUsuario)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CarritoDTO>>() {});
    }
}
