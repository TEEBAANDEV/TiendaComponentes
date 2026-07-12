package com.example.inv_cliente.client;


import com.example.inv_cliente.model.InventarioDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;


@Service
public class InventarioClient {

    private final WebClient webClient;


    public InventarioClient(@Qualifier("inventarioWebClient") WebClient webClient){

        this.webClient = webClient;
    }

    public Mono<InventarioDTO> obtenerStock(Long idProducto){
        return webClient.get()
                .uri("/{id}",idProducto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,"Stock no disponible en este momento"))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new ResponseStatusException(HttpStatus.BAD_GATEWAY, "El servicio de inventario está caído"))
                )
                .bodyToMono(InventarioDTO.class);
    }
}