package com.example.ventas.controller;

import com.example.ventas.model.Venta;
import com.example.ventas.service.VentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/Ventas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ventas Controller", description = "Endpoints para la gestión y procesamiento de ventas")
public class VentasController {

    private final VentaService ventaService;

    private Mono<Venta> addLinks(Venta venta) {
        if (venta == null || venta.getId() == null) {
            return Mono.just(venta);
        }
        return linkTo(methodOn(VentasController.class).obtenerVentaPorId(venta.getId()))
                .withSelfRel()
                .toMono()
                .flatMap(selfLink -> linkTo(methodOn(VentasController.class).listarVentas())
                        .withRel("listar_ventas")
                        .toMono()
                        .map(listLink -> {
                            venta.add(selfLink);
                            venta.add(listLink);
                            return venta;
                        }))
                .defaultIfEmpty(venta);
    }

    @PostMapping("/comprar/{idUsuario}")
    @Operation(summary = "Generar compra", description = "Procesa y genera una compra para un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Compra generada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error al procesar la compra")
    })
    public Mono<ResponseEntity<Object>> generarCompra(@PathVariable Long idUsuario) {
        log.info("Procesando compra para el usuario con ID: {}", idUsuario);
        return ventaService.procesarVenta(idUsuario)
                .flatMap(this::addLinks)
                .map(ventaGuardada -> ResponseEntity.status(HttpStatus.CREATED).<Object>body(ventaGuardada))
                .onErrorResume(e -> {
                    log.error("Error al procesar la compra para usuario ID: {}", idUsuario, e);
                    return Mono.just(ResponseEntity.badRequest().body(e.getMessage()));
                });
    }

    @GetMapping
    @Operation(summary = "Listar ventas", description = "Obtiene la lista completa de todas las ventas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ventas obtenida exitosamente")
    })
    public Flux<Venta> listarVentas(){
        log.info("Solicitud para listar todas las ventas recibida");
        return Flux.fromIterable(ventaService.obtenerVentas())
                .flatMap(this::addLinks);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar venta", description = "Elimina una venta por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Venta eliminada exitosamente")
    })
    public Mono<ResponseEntity<Void>> eliminarVenta(@PathVariable Long id){
        log.info("Eliminando venta con ID: {}", id);
        return Mono.fromRunnable(() -> ventaService.eliminarVenta(id))
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener venta por ID", description = "Obtiene una venta específica según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    public Mono<ResponseEntity<Venta>> obtenerVentaPorId(@PathVariable Long id){
        log.info("Buscando venta con ID: {}", id);
        return Mono.justOrEmpty(ventaService.obtenerVentaPorId(id))
                .flatMap(this::addLinks)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de venta", description = "Actualiza el estado de una venta existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    public Mono<ResponseEntity<Venta>> actualizarEstado(@PathVariable Long id, @RequestParam String nuevoEstado){
        log.info("Actualizando estado de venta ID: {} a {}", id, nuevoEstado);
        return Mono.fromCallable(() -> ventaService.actualizarEstado(id, nuevoEstado))
                .flatMap(this::addLinks)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error al actualizar estado de la venta ID: {}", id, e);
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }
}
