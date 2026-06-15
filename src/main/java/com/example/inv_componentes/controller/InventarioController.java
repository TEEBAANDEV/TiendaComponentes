package com.example.inv_componentes.controller;

import com.example.inv_componentes.client.ProductoClient;
import com.example.inv_componentes.model.Inventario;
import com.example.inv_componentes.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder; // Importación explícita para WebFlux
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Controlador reactivo para la gestión y control de stock de productos")
@SecurityRequirement(name = "bearerAuth")
public class InventarioController {

    private final ProductoClient productoClient;
    private final InventarioService service;

    @Operation(
            summary = "Registrar nuevo stock en inventario",
            description = "Valida de forma asíncrona la existencia del producto en el microservicio externo antes de inicializar su inventario."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro de inventario creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Cuerpo de la petición o datos de validación inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el microservicio externo")
    })
    @PostMapping
    public Mono<ResponseEntity<EntityModel<Inventario>>> crearInventario(@Valid @RequestBody Inventario inventario){
        return productoClient.obtenerProducto(inventario.getIdProducto())
                .flatMap(producto -> {
                    inventario.setNombreProducto(producto.getNombre());
                    inventario.setDescripcion(producto.getDescripcion());
                    Inventario guardado = service.save(inventario);

                    return WebFluxLinkBuilder.linkTo(
                            WebFluxLinkBuilder.methodOn(InventarioController.class).listar()
                    ).withRel("inventario_global").toMono().map(link -> ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(guardado, link)));
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).<EntityModel<Inventario>>build()));
    }

    @Operation(
            summary = "Listar todo el inventario disponible",
            description = "Retorna el listado de existencias enriqueciendo los datos del producto asíncronamente en tiempo real."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de stock obtenido de forma exitosa"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping
    public Mono<ResponseEntity<CollectionModel<EntityModel<Inventario>>>> listar(){
        List<Inventario> listaInventario = service.listar();

        return Flux.fromIterable(listaInventario)
                .flatMap(item -> productoClient.obtenerProducto(item.getIdProducto())
                        .map(producto -> {
                            item.setNombreProducto(producto.getNombre());
                            item.setDescripcion(producto.getDescripcion());
                            return item;
                        })
                        .defaultIfEmpty(item)
                )
                .flatMap(item -> WebFluxLinkBuilder.linkTo(
                        WebFluxLinkBuilder.methodOn(InventarioController.class).listar()
                ).withSelfRel().toMono().map(link -> EntityModel.of(item, link)))
                .collectList()
                .flatMap(listaConLinks -> WebFluxLinkBuilder.linkTo(
                        WebFluxLinkBuilder.methodOn(InventarioController.class).listar()
                ).withSelfRel().toMono().map(selfLink -> ResponseEntity.ok(CollectionModel.of(listaConLinks, selfLink))));
    }

    @Operation(
            summary = "Descontar stock de un producto",
            description = "Reduce las unidades físicas de un componente tras confirmarse un proceso de compra o despacho."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock rebajado correctamente del almacén"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "El producto especificado no cuenta con registros de inventario"),
            @ApiResponse(responseCode = "500", description = "Error interno - Stock insuficiente para realizar el descuento solicitado")
    })
    @PutMapping("/descontar")
    public Mono<ResponseEntity<Void>> descontarStock(@RequestParam Long idProducto, @RequestParam int cantidad){
        // Envolvemos el método síncrono del servicio en un flujo diferido no bloqueante
        return Mono.fromRunnable(() -> service.descontarStock(idProducto, cantidad))
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }
}