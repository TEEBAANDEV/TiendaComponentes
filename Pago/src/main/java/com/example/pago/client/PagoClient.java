package com.example.pago.client;

import com.example.pago.model.Pago;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PagoClient {
    private final WebClient webClient;

    public Mono<Pago> obtenerPago(Long id){
        return webClient.get().uri("/{id}", id)
                .retrieve().onStatus(HttpStatusCode::is4xxClientError,
                        response-> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado :c")))
                .bodyToMono(Pago.class);
    }

}
