package com.example.envios.controller;

import com.example.envios.Service.EnvioService;
import com.example.envios.client.ReciboClient;
import com.example.envios.client.UsuarioClient;
import com.example.envios.model.Envio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/envio")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Envío Controller", description = "Endpoints para la gestión y despacho de envíos")
public class EnvioController {

    @Autowired
    private final EnvioService service;
    
    @Autowired
    private final ReciboClient reciboClient;
    
    @Autowired
    private final UsuarioClient usuarioClient;

    private Mono<Envio> addLinks(Envio envio) {
        if (envio == null || envio.getId() == null) {
            return Mono.just(envio);
        }
        return linkTo(methodOn(EnvioController.class).buscarPorId(envio.getId()))
                .withSelfRel()
                .toMono()
                .flatMap(selfLink -> linkTo(methodOn(EnvioController.class).listar())
                        .withRel("listar_envios")
                        .toMono()
                        .map(listLink -> {
                            envio.add(selfLink);
                            envio.add(listLink);
                            return envio;
                        }))
                .defaultIfEmpty(envio);
    }

    @GetMapping
    @Operation(summary = "Listar todos los envíos", description = "Obtiene la lista completa de todos los envíos registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de envíos recuperada exitosamente")
    })
    public Flux<Envio> listar(){
        log.info("Solicitud para listar todos los envíos recibida");
        return service.listar()
                .flatMap(this::addLinks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar envío por ID", description = "Obtiene los detalles de un envío específico según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Envío encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    })
    public Mono<ResponseEntity<Envio>> buscarPorId(@PathVariable Long id){
        log.info("Buscando envío con ID: {}", id);
        return service.findById(id)
                .flatMap(this::addLinks)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/generar/{idRecibo}")
    @Operation(summary = "Despachar envío", description = "Genera y registra el envío a partir del ID de un recibo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Envío generado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Recibo o dirección de usuario no encontrada"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible debido a un error")
    })
    public Mono<ResponseEntity<Envio>> despacharenvio(@PathVariable Long idRecibo){
        log.info("Iniciando despacho de envío para el recibo ID: {}", idRecibo);
        return reciboClient.obtenerRecibo(idRecibo)
                .switchIfEmpty(Mono.error(new RuntimeException("Recibo no encontrado con ID: " + idRecibo)))
                .flatMap(reciboDTO -> {
                    log.info("Recibo encontrado. Obteniendo información de usuario ID: {}", reciboDTO.getIdUsuario());
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
                .flatMap(this::addLinks)
                .map(envioGuardado -> {
                    log.info("Envío creado exitosamente con ID: {}", envioGuardado.getId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(envioGuardado);
                })
                .onErrorResume(e -> {
                    log.error("Error al crear envio: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
                });
    }
}
