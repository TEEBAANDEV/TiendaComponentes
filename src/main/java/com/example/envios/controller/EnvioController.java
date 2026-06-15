package com.example.envios.controller;

import com.example.envios.Service.EnvioService;
import com.example.envios.client.ReciboClient;
import com.example.envios.client.UsuarioClient;
import com.example.envios.model.Envio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/envio")
@RequiredArgsConstructor
@Tag(name = "Envíos", description = "Controlador reactivo para la gestión y despacho de envíos")
@SecurityRequirement(name = "bearerAuth")
public class EnvioController {

    private final EnvioService service;
    private final ReciboClient reciboClient;
    private final UsuarioClient usuarioClient;

    @Operation(summary = "Listar todos los envíos", description = "Retorna un flujo de todos los envíos registrados con soporte de enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de envíos obtenida correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Prohibido")
    })
    @GetMapping
    public Mono<ResponseEntity<CollectionModel<EntityModel<Envio>>>> listar() {
        return service.listar()
                .flatMap(envio ->
                        // SOLUCIÓN ERROR LÍNEA 48: Convertimos el linkBuilder a Mono<Link> explícitamente antes de armar el EntityModel
                        linkTo(methodOn(EnvioController.class).buscarPorId(envio.getId())).withSelfRel().toMono()
                                .map(link -> EntityModel.of(envio, link))
                )
                .collectList()
                .flatMap(lista ->
                        // SOLUCIÓN ERROR LÍNEA 51: Convertimos el link de la colección a Mono<Link> de forma asíncrona
                        linkTo(methodOn(EnvioController.class).listar()).withSelfRel().toMono()
                                .map(selfLink -> ResponseEntity.ok(CollectionModel.of(lista, selfLink)))
                );
    }

    @Operation(summary = "Buscar envío por ID", description = "Obtiene los detalles de un envío específico mediante su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Envío encontrado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EntityModel<Envio>>> buscarPorId(@PathVariable Long id) {
        return service.findById(id)
                .flatMap(envio -> {
                    Mono<Link> selfLinkMono = linkTo(methodOn(EnvioController.class).buscarPorId(id)).withSelfRel().toMono();
                    Mono<Link> listarLinkMono = linkTo(methodOn(EnvioController.class).listar()).withRel("envios_global").toMono();

                    return Mono.zip(selfLinkMono, listarLinkMono)
                            .map(tuple -> EntityModel.of(envio, tuple.getT1(), tuple.getT2()));
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Generar envío desde Recibo", description = "Valida la existencia del recibo y del usuario para procesar el envío.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Envío generado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "503", description = "Servicio externo no disponible")
    })
    @PostMapping("/generar/{idRecibo}")
    public Mono<ResponseEntity<EntityModel<Envio>>> despacharenvio(@PathVariable Long idRecibo) {
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
                .flatMap(envioGuardado ->
                        // SOLUCIÓN ERROR LÍNEA 111: Aseguramos el uso de .toMono() en el flujo final de guardado
                        linkTo(methodOn(EnvioController.class).buscarPorId(envioGuardado.getId())).withSelfRel().toMono()
                                .map(link -> ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(envioGuardado, link)))
                )
                .onErrorResume(e -> {
                    System.err.println("Error al crear envio: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).<EntityModel<Envio>>build());
                });
    }
}