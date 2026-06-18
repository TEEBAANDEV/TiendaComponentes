package com.example.analitica.controller;

import com.example.analitica.client.ProductoClient;
import com.example.analitica.client.UsuarioClient;
import com.example.analitica.model.Resena;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import com.example.analitica.servicio.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/resenas")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class ResenaController {

    private final ResenaService resenaService;
    private final ProductoClient productoClient;
    private final UsuarioClient usuarioClient;

    public ResenaController(ResenaService resenaService, ProductoClient productoClient, UsuarioClient usuarioClient) {
        this.resenaService = resenaService;
        this.productoClient = productoClient;
        this.usuarioClient = usuarioClient;
    }

    @GetMapping("/{productoId}/promedio")
    @Operation(
            summary = "Obtener promedio de calificaciones",
            description = "Calcula el promedio de estrellas y el total de votos para un producto dado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Promedio calculado exitosamente"
            )
    })
    public Mono<Map<String, Object>> getPromedio(@PathVariable Long productoId) {
        log.info("Obteniendo promedio de calificaciones para productoId: {}", productoId);
        return resenaService.obtenerSoloPromedio(productoId);
    }

    @GetMapping
    @Operation(
            summary = "Obtener comentarios",
            description = "Retorna todos los comentarios en el sistema de manera reactiva con enlaces HATEOAS"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Comentarios obtenidos correctamente"
            )
    })
    public Flux<Resena> obtenerComentarios() {
        log.info("Solicitando todos los comentarios de manera reactiva");
        return Flux.defer(() -> Flux.fromIterable(resenaService.obtenerComentarios()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(resena -> WebFluxLinkBuilder.linkTo(
                        WebFluxLinkBuilder.methodOn(ResenaController.class).obtenerResenaPorId(resena.getId())
                )
                .withSelfRel()
                .toMono()
                .map(link -> {
                    resena.add(link);
                    return resena;
                }));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener reseña por ID",
            description = "Busca una reseña específica por su identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reseña encontrada"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reseña no encontrada"
            )
    })
    public Mono<ResponseEntity<Resena>> obtenerResenaPorId(@PathVariable Long id) {
        log.info("Buscando reseña con ID: {}", id);
        return Mono.fromCallable(() -> resenaService.obtenerPorId(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(resenaOpt -> {
                    if (resenaOpt.isPresent()) {
                        Resena resena = resenaOpt.get();
                        return WebFluxLinkBuilder.linkTo(
                                WebFluxLinkBuilder.methodOn(ResenaController.class).obtenerResenaPorId(id)
                        )
                        .withSelfRel()
                        .toMono()
                        .map(link -> {
                            resena.add(link);
                            return ResponseEntity.ok(resena);
                        });
                    } else {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                });
    }

    @Operation(
            summary = "Crear reseña",
            description = "Crea una nueva reseña en el sistema y valida la existencia del producto y el usuario"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Reseña creada correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Sin permisos"
            )
    })
    @PostMapping("/comentar/{productoId}")
    public Mono<ResponseEntity<Resena>> crearResena(@Valid @RequestBody Resena item) {
        log.info("Iniciando creación de reseña para productoId: {}", item.getProductoId());
        return Mono.zip(
                        productoClient.obtenerProducto(item.getProductoId()),
                        usuarioClient.obtenerUsuario(item.getUsuarioId())
                ).flatMap(tuple2 -> {
                    Resena resena = new Resena();
                    resena.setProductoId(item.getProductoId());
                    resena.setUsuarioId(item.getUsuarioId());
                    resena.setComentario(item.getComentario());
                    resena.setCalificacion(item.getCalificacion());
                    return Mono.fromCallable(() -> resenaService.crearResena(resena))
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .flatMap(resena -> WebFluxLinkBuilder.linkTo(
                        WebFluxLinkBuilder.methodOn(ResenaController.class).obtenerResenaPorId(resena.getId())
                )
                .withSelfRel()
                .toMono()
                .map(link -> {
                    resena.add(link);
                    return ResponseEntity.status(HttpStatus.CREATED).body(resena);
                }));
    }
}
