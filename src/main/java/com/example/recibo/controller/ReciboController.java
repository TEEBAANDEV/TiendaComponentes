package com.example.recibo.controller;

import com.example.recibo.client.VentaClient;
import com.example.recibo.model.Recibo;
import com.example.recibo.service.ReciboService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Collectors;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/recibo")
@RequiredArgsConstructor
@Slf4j
public class ReciboController {

    @Autowired
    private final VentaClient ventaClient;

    @Autowired
    private final ReciboService service;

    @Operation(summary = "Generar un nuevo recibo", description = "Genera y guarda un recibo a partir del detalle de una venta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Recibo generado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada o error al generar recibo")
    })
    @PostMapping("/generar/{idVenta}")
    public Mono<ResponseEntity<Recibo>> crearRecibo(@PathVariable Long idVenta){
        log.info("Iniciando generación de recibo para venta ID: {}", idVenta);
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
                    log.info("Recibo guardado exitosamente con ID: {}", guardado.getIdRecibo());
                    
                    return linkTo(methodOn(ReciboController.class).obtenerPorId(guardado.getIdRecibo())).withSelfRel().toMono()
                            .flatMap(selfLink -> linkTo(methodOn(ReciboController.class).listarRecibos()).withRel("recibos").toMono()
                                    .map(recibosLink -> {
                                        guardado.add(selfLink);
                                        guardado.add(recibosLink);
                                        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
                                    })
                            );
                })
                .onErrorResume(e -> {
                    log.error("Error al generar recibo para la venta {}: {}", idVenta, e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                });
    }

    @Operation(summary = "Listar todos los recibos", description = "Retorna una lista reactiva (Flux) de todos los recibos registrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa")
    })
    @GetMapping
    public Flux<Recibo> listarRecibos(){
        log.info("Listando todos los recibos");
        return Flux.fromIterable(service.listar())
                .flatMap(recibo -> 
                    linkTo(methodOn(ReciboController.class).obtenerPorId(recibo.getIdRecibo())).withSelfRel().toMono()
                        .map(link -> {
                            recibo.add(link);
                            return recibo;
                        })
                );
    }

    @Operation(summary = "Obtener un recibo por su ID", description = "Busca un recibo específico mediante su identificador único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recibo encontrado"),
        @ApiResponse(responseCode = "404", description = "Recibo no encontrado")
    })
    @GetMapping("/{idRecibo}")
    public Mono<ResponseEntity<Recibo>> obtenerPorId(@PathVariable Long idRecibo) {
        log.info("Buscando recibo por ID: {}", idRecibo);
        return Mono.justOrEmpty(service.obtenerPorId(idRecibo))
                .flatMap(recibo -> 
                    linkTo(methodOn(ReciboController.class).obtenerPorId(recibo.getIdRecibo())).withSelfRel().toMono()
                        .flatMap(selfLink -> 
                            linkTo(methodOn(ReciboController.class).listarRecibos()).withRel("recibos").toMono()
                                .map(recibosLink -> {
                                    recibo.add(selfLink);
                                    recibo.add(recibosLink);
                                    return recibo;
                                })
                        )
                )
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

