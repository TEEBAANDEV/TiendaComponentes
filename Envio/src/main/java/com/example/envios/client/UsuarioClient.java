package com.example.envios.client;


import com.example.envios.modelo.UsuarioDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(@Qualifier("webUserClient") WebClient webClient){
        this.webClient = webClient;
    }

    public Mono<UsuarioDTO> obtenerUsuario(Long id){
        return webClient.get().uri("/{id}",id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"))
                )
                .bodyToMono(UsuarioDTO.class);
    }
}
