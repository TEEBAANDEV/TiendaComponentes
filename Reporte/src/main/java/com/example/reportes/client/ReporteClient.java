package com.example.reportes.client;


import com.example.reportes.model.Reporte;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReporteClient {
    private final WebClient webClient;

    public Mono<Reporte> obtenerReporte(Long id){
        return webClient.get().uri("/{id}", id).retrieve().onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado"))).bodyToMono(Reporte.class);
    }
}
