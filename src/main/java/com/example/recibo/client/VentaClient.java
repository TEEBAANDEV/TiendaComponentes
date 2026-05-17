package com.example.recibo.client;

import com.example.recibo.model.VentaDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class VentaClient {

    private final WebClient webClient;

    public VentaClient(@Qualifier("ventasWebClient") WebClient webClient){
        this.webClient = webClient;
    }


    /* estamos pidiendo una peticion para consultar
    el datalle de venta mediante su id mapeando la respusta con VentaDTO
    si no lo encuentra retornará un error (Avatar la leyenda de Aang carrea mi estabilidad emocional)
     */

    public Mono<VentaDTO> obtenerDetalleVenta(Long idVenta){
        return webClient.get()
                .uri("/{id}", idVenta)
                .retrieve()   //retrieve solo me hace pensar en los perritos golden retriever
                .onStatus(HttpStatusCode::is4xxClientError,response -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada")))
                .bodyToMono(VentaDTO.class);
    }
}
