package com.example.recibo.controller;

import com.example.recibo.client.VentaClient;
import com.example.recibo.model.Recibo;
import com.example.recibo.service.ReciboService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder; // Importación explícita para evitar ambigüedades
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/recibo")
@RequiredArgsConstructor
@Tag(name = "Recibos", description = "Controlador reactivo para la generación y consulta de comprobantes fiscales")
@SecurityRequirement(name = "bearerAuth")
public class ReciboController {


    private final VentaClient ventaClient;
    private final ReciboService service;

    @Operation(
            summary = "Generar recibo desde una Venta",
            description = "Consulta síncronamente los detalles de una venta en un microservicio externo, procesa los ítems en un hilo seguro y guarda el recibo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recibo generado e impreso correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta o ID de venta inválido"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Venta no localizada o error en procesamiento")
    })
    @PostMapping("/generar/{idVenta}")
    public Mono<ResponseEntity<EntityModel<Recibo>>> crearRecibo(@PathVariable Long idVenta){
        return ventaClient.obtenerDetalleVenta(idVenta)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(venta -> {
                    Recibo nuevoRecibo = new Recibo();
                    nuevoRecibo.setIdVenta(venta.getId());
                    nuevoRecibo.setIdUsuario(venta.getIdUsuario());

                    String glosaCompleta = venta.getDetalles().stream()
                            .map(d -> d.getCantidad() + "x " + d.getNombreProducto() + " (" + d.getDescripcion() + ")")
                            .collect(Collectors.joining(" | "));

                    nuevoRecibo.setNombreProducto(glosaCompleta);
                    nuevoRecibo.setMontoTotal(venta.getTotal());
                    nuevoRecibo.setMetodoPago("TARJETA");
                    nuevoRecibo.setFechaEmision(venta.getFecha());

                    Recibo guardado = service.save(nuevoRecibo);

                    return WebFluxLinkBuilder.linkTo(
                            WebFluxLinkBuilder.methodOn(ReciboController.class).obtenerPorId(guardado.getIdRecibo())
                    ).withSelfRel().toMono().map(link -> ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(guardado, link)));
                })
                .onErrorResume(e -> {
                    System.err.println("Error en recibo: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).<EntityModel<Recibo>>build());
                });
    }

    @Operation(summary = "Listar todos los recibos", description = "Retorna una colección asíncrona de todos los recibos emitidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de recibos obtenida con éxito"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping
    public Mono<ResponseEntity<CollectionModel<EntityModel<Recibo>>>> listarRecibos(){
        return Flux.fromIterable(service.listar())
                .flatMap(recibo -> WebFluxLinkBuilder.linkTo(
                        WebFluxLinkBuilder.methodOn(ReciboController.class).obtenerPorId(recibo.getIdRecibo())
                ).withSelfRel().toMono().map(link -> EntityModel.of(recibo, link)))
                .collectList()
                .flatMap(listaConLinks -> WebFluxLinkBuilder.linkTo(
                        WebFluxLinkBuilder.methodOn(ReciboController.class).listarRecibos()
                ).withSelfRel().toMono().map(selfLink -> ResponseEntity.ok(CollectionModel.of(listaConLinks, selfLink))));
    }

    @Operation(summary = "Obtener recibo por ID", description = "Busca un recibo específico mediante su identificador fiscal único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recibo localizado"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "El recibo solicitado no existe")
    })
    @GetMapping("/{idRecibo}")
    public Mono<ResponseEntity<EntityModel<Recibo>>> obtenerPorId(@PathVariable Long idRecibo) {
        return Mono.justOrEmpty(service.obtenerPorId(idRecibo))
                .flatMap(recibo -> {
                    var selfLink = WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(ReciboController.class).obtenerPorId(idRecibo)).withSelfRel().toMono();
                    var globalLink = WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(ReciboController.class).listarRecibos()).withRel("recibos_global").toMono();

                    return Mono.zip(selfLink, globalLink)
                            .map(tuple -> EntityModel.of(recibo, tuple.getT1(), tuple.getT2()));
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}