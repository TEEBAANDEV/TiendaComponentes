package com.example.ventas.client;


import com.example.ventas.model.UsuarioDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class UserClient {

    private final WebClient webClient;

    public UserClient(@Qualifier("usuarioWebClient") WebClient webClient){
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
