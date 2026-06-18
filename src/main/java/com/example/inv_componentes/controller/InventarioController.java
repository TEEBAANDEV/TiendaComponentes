package com.example.inv_componentes.controller;

import com.example.inv_componentes.client.ProductoClient;
import com.example.inv_componentes.model.Inventario;
import com.example.inv_componentes.service.InventarioService;
import jakarta.validation.Valid;
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

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
@Slf4j
public class InventarioController {

    private final ProductoClient productoClient;

    @Autowired
    private final InventarioService service;

    @PostMapping
    @Operation(summary = "Crear inventario", description = "Crea un registro de inventario para un producto si este existe.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Inventario creado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public Mono<ResponseEntity<Inventario>> crearInventario(@Valid @RequestBody Inventario inventario){
        log.info("Creando inventario para el producto: {}", inventario.getIdProducto());
        return productoClient.obtenerProducto(inventario.getIdProducto())
                .map(producto -> {
                    inventario.setNombreProducto(producto.getNombre());
                    inventario.setDescripcion(producto.getDescripcion());
                    return service.save(inventario);
                })
                .flatMap(guardado -> Mono.zip(
                        linkTo(methodOn(InventarioController.class).crearInventario(null)).withSelfRel().toMono(),
                        linkTo(methodOn(InventarioController.class).listar()).withRel("listar").toMono()
                ).map(tuple -> {
                    guardado.add(tuple.getT1());
                    guardado.add(tuple.getT2());
                    return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
                }))
                .onErrorResume(e -> {
                    log.error("Error al crear inventario o producto no encontrado: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                });
    }

    @GetMapping
    @Operation(summary = "Listar inventario", description = "Obtiene todos los registros de inventario con información de los productos.")
    @ApiResponse(responseCode = "200", description = "Lista de inventario obtenida exitosamente")
    public Flux<Inventario> listar(){
        log.info("Listando todos los items de inventario");
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
                .flatMap(item -> Mono.zip(
                        linkTo(methodOn(InventarioController.class).listar()).withSelfRel().toMono(),
                        linkTo(methodOn(InventarioController.class).crearInventario(null)).withRel("crear").toMono()
                ).map(tuple -> {
                    item.add(tuple.getT1());
                    item.add(tuple.getT2());
                    return item;
                }));
    }

    @PutMapping("/descontar")
    @Operation(summary = "Descontar stock", description = "Descuenta una cantidad específica de stock de un producto.")
    @ApiResponse(responseCode = "200", description = "Stock descontado exitosamente")
    public ResponseEntity<Void> descontarStock(@RequestParam Long idProducto, @RequestParam int cantidad){
        log.info("Descontando stock para producto: {}, cantidad: {}", idProducto, cantidad);
        service.descontarStock(idProducto, cantidad);
        return ResponseEntity.ok().build();
    }
}
