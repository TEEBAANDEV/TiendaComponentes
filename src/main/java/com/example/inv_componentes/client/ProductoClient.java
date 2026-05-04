package com.example.inv_componentes.client;

import com.example.inv_componentes.model.Producto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductoClient {

    private final WebClient webClient;

    public Mono<Producto> obtenerProducto(Long id){
        return webClient.get()
                .uri("/{id}",id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado"))
                )
                .bodyToMono(Producto.class);
    }

}
