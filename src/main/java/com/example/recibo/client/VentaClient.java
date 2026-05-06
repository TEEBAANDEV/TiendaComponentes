package com.example.recibo.client;

import com.example.recibo.model.VentaDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class VentaClient {

    private final WebClient webClient;

    public VentaClient(@Qualifier("ventasWebClient") WebClient webClient){
        this.webClient = webClient;
    }

    public Mono<VentaDTO> obtenerDetalleVenta(Long idVenta){
        return webClient.get()
                .uri("/{id}", idVenta)
                .retrieve()
                .bodyToMono(VentaDTO.class);
    }
}
