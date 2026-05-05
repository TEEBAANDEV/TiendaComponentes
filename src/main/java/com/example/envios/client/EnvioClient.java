package com.example.envios.client;

import com.example.envios.modelo.Envio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EnvioClient {


        private final WebClient webClient;

        public Mono<Envio> obtenerEnvio(Long id){
            return webClient.get().uri("/{id}", id).
                    retrieve().onStatus(HttpStatusCode::is4xxClientError, response ->
                    Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Envio no encontrado :c")))
                    .bodyToMono(Envio.class);
        }
    }

