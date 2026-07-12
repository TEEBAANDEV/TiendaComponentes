package com.example.analitica.controller;

import com.example.analitica.client.ProductoClient;
import com.example.analitica.client.UsuarioClient;
import com.example.analitica.model.Resena;
import com.example.analitica.servicio.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/resenas")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
@Tag(name = "Comentarios y Reseñas (Analítica)", description = "Gestion de reseñas de productos")
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
            @ApiResponse(responseCode = "200", description = "Promedio calculado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"productoId\": 102, \"promedioEstrellas\": 4.7, \"totalVotos\": 24}"))),
            @ApiResponse(responseCode = "400", description = "ID de producto inválido"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el catálogo"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar el cálculo estadístico")
    })
    public ResponseEntity<Map<String, Object>> getPromedio(@PathVariable Long productoId) {

        log.info("Obteniendo promedio de calificaciones para productoId: {}", productoId);

        Map<String, Object> resultado = resenaService.obtenerSoloPromedio(productoId).block();

        return ResponseEntity.ok(resultado);
    }

    @GetMapping
    @Operation(
            summary = "Obtener comentarios",
            description = "Retorna todos los comentarios del sistema con enlaces HATEOAS"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comentarios obtenidos correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Resena.class))),
            @ApiResponse(responseCode = "500", description = "Error interno al recuperar el listado global")
    })
    public ResponseEntity<List<Resena>> obtenerComentarios() {

        log.info("Solicitando todos los comentarios");

        List<Resena> comentarios = resenaService.obtenerComentarios();

        comentarios.forEach(resena -> {

            resena.add(
                    linkTo(methodOn(ResenaController.class)
                            .obtenerResenaPorId(resena.getId()))
                            .withSelfRel()
            );

        });

        return ResponseEntity.ok(comentarios);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener reseña por ID",
            description = "Busca una reseña específica por su identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reseña encontrada y retornada con éxito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Resena.class,
                                    example = "{\"id\": 1, \"productoId\": 102, \"usuarioId\": 10, \"comentario\": \"Excelente rendimiento y temperaturas.\", \"calificacion\": 5}"))
            ),
            @ApiResponse(responseCode = "400", description = "El ID de la reseña provisto tiene un formato incorrecto"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reseña no encontrada en los registros"
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Resena>> obtenerResenaPorId(@PathVariable Long id) {
        log.info("Buscando reseña con ID: {}", id);
        return Mono.fromCallable(() -> resenaService.obtenerPorId(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(resenaOpt -> {
                    if (resenaOpt.isPresent()) {
                        Resena resena = resenaOpt.get();
                        try {
                            resena.add(linkTo(methodOn(ResenaController.class).obtenerResenaPorId(id)).withSelfRel());
                        } catch (Exception e) {
                            log.error("Error building link: {}", e.getMessage());
                        }
                        return ResponseEntity.ok(resena);
                    } else {
                        return ResponseEntity.notFound().build();
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
                    description = "Reseña creada y guardada correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Resena.class,
                                    example = "{\"id\": 1, \"productoId\": 102, \"usuarioId\": 10, \"comentario\": \"Excelente rendimiento y temperaturas.\", \"calificacion\": 5}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o fallas de validación de campos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado, se requiere un token JWT válido en la cabecera"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Sin permisos suficientes para comentar"
            ),
            @ApiResponse(responseCode = "404", description = "El usuarioId o productoId especificado no existe de forma remota"),
            @ApiResponse(responseCode = "500", description = "Error interno al persistir la reseña")
    })
    @PostMapping("/comentar/{productoId}")
    public Mono<ResponseEntity<Resena>> crearResena(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload para la creación de la reseña",
                    content = @Content(schema = @Schema(implementation = Resena.class,
                            example = "{\"productoId\": 102, \"usuarioId\": 10, \"comentario\": \"Excelente rendimiento y temperaturas.\", \"calificacion\": 5}")))
            @Valid @RequestBody Resena item) {
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
                .map(resena -> {
                    try {
                        resena.add(linkTo(methodOn(ResenaController.class).obtenerResenaPorId(resena.getId())).withSelfRel());
                    } catch (Exception e) {
                        log.error("Error building link: {}", e.getMessage());
                    }
                    return ResponseEntity.status(HttpStatus.CREATED).body(resena);
                });
    }
    }

