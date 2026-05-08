package com.example.reportes.client;

import com.example.reportes.model.ReciboDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class ReciboClient{
    private final WebClient webClient;

    public ReciboClient(@Qualifier("reciboWebClient") WebClient webClient){
        this.webClient = webClient;
    }

    public Mono<ReciboDTO> obtenerRecibo(Long idRecibo){
        return webClient.get()
                .uri("/{id}", idRecibo)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado")))
                .bodyToMono(ReciboDTO.class);
    }
}
