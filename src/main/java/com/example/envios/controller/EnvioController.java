package com.example.envios.controller;

import com.example.envios.Service.EnvioService;
import com.example.envios.client.ReciboClient;
import com.example.envios.client.UsuarioClient;
import com.example.envios.modelo.Envio;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/envio")
@RequiredArgsConstructor
public class EnvioController {

    @Autowired
    private final EnvioService service;
    private final ReciboClient reciboClient;
    @Autowired
    private UsuarioClient usuarioClient;

    @GetMapping
    public Flux<Envio> listar(){
        return service.listar();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Envio>> buscarPorId(@PathVariable Long id){
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/generar/{idRecibo}")
    public Mono<ResponseEntity<Envio>> despacharenvio(@PathVariable Long idRecibo){
        return reciboClient.obtenerRecibo(idRecibo)
                .switchIfEmpty(Mono.error(new RuntimeException("Recibo no encontrado con ID: " + idRecibo)))
                .flatMap(reciboDTO -> {
                    return usuarioClient.obtenerUsuario(reciboDTO.getIdUsuario())
                            .switchIfEmpty(Mono.error(new RuntimeException("No se encontró dirección para el usuario")))
                            .map(direccion -> {
                                Envio envio = new Envio();
                                envio.setIdRecibo(idRecibo);
                                envio.setIdUsuario(reciboDTO.getIdUsuario());
                                envio.setDireccionDestino(direccion.getDireccion());
                                envio.setEmpresaTransporte(envio.getEmpresaTransporte());
                                envio.setCodigoSeguimiento(envio.getCodigoSeguimiento());
                                envio.setEstadoEnvio("PROCESANDO_LOGISTICA");
                                envio.setFechaActalizacion(envio.getFechaActalizacion());
                                envio.setFechaDespacho(LocalDateTime.now());
                                return envio;
                            });
                })
                .flatMap(service::save)
                .map(envioGuardado -> ResponseEntity.status(HttpStatus.CREATED).body(envioGuardado))
                .onErrorResume(e -> {
                    System.err.println("Error al crear envio: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
                });
    }

    @GetMapping("/estado/{idRecibo}")
    public Mono<ResponseEntity<Envio>> consultarEstado(@PathVariable Long idRecibo) {
        return service.findById(idRecibo)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }



}
