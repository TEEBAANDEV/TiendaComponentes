package com.example.inv_componentes.client;

import com.example.inv_componentes.model.Producto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
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
    private final HttpServletRequest request;

    public Mono<Producto> obtenerProducto(Long id){
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        return webClient.get()
                .uri("/{id}",id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado"))
                )
                .bodyToMono(Producto.class);
    }

}
