package com.example.envios.client;


import com.example.envios.modelo.ReciboDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ReciboClient {

    private final WebClient webClient;

    public ReciboClient(@Qualifier("reciboWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ReciboDTO> obtenerRecibo(Long idRecibo) {
        return webClient.get()
                .uri("/{id}", idRecibo)
                .retrieve()
                .bodyToMono(ReciboDTO.class);
    }
}
