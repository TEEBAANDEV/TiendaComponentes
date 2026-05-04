package com.example.ventas.client;


import com.example.ventas.model.CarritoDTO;
import com.example.ventas.model.ProductoDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class CarritoClient {

    private final WebClient webClient;

    public CarritoClient(@Qualifier("carritoWebClient") WebClient webClient){
        this.webClient = webClient;
    }

    public Mono<CarritoDTO> obtenerProducto(Long id){
        return webClient.get()
                .uri("/{id}",id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,"El carrito no existe"))
                )
                .bodyToMono(CarritoDTO.class);
    }
}
