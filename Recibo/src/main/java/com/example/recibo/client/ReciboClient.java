package com.example.recibo.client;

import com.example.recibo.model.Recibo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReciboClient {
    private final WebClient webClient;

    public Mono<Recibo> obtenerRecibo(Long id){
        return webClient.get().uri("/{id}", id).retrieve().onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Recibo no encontrado"))).bodyToMono(Recibo.class);
    }
}
