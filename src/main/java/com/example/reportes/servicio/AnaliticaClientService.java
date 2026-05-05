package com.example.reportes.servicio;

import com.example.reportes.model.EventoAnaliticaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnaliticaClientService {

    private final WebClient webClient;

    public AnaliticaClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Void> registrarEvento(String tipo, Double valor, String metadata) {
        EventoAnaliticaDTO evento = new EventoAnaliticaDTO(tipo, valor);

        return webClient.post()
                .uri("/api/analitica")
                .bodyValue(evento)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Long> contarEventosPorTipo(String tipo) {
        return webClient.get()
                .uri("/api/analitica/tipo/{tipo}", tipo)
                .retrieve()
                .bodyToFlux(EventoAnaliticaDTO.class)
                .count();
    }
}
