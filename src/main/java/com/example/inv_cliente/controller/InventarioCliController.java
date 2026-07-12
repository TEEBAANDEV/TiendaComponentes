package com.example.inv_cliente.controller;

import com.example.inv_cliente.client.ProductoClient;
import com.example.inv_cliente.client.UsuarioClient;
import com.example.inv_cliente.model.Inventario_cliente;
import com.example.inv_cliente.model.Producto;
import com.example.inv_cliente.service.InventarioCliService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/carrito")
@RequiredArgsConstructor
@Validated
@Slf4j
public class InventarioCliController {

    private final ProductoClient productoClient;

    @Autowired
    private final InventarioCliService service;
    @Autowired
    private final UsuarioClient usuarioClient;

    private Mono<Inventario_cliente> agregarLinks(Inventario_cliente item) {
        try {
            item.add(linkTo(methodOn(InventarioCliController.class).verCarrito(item.getIdUsuario())).withSelfRel());
            item.add(linkTo(methodOn(InventarioCliController.class).eliminarItem(item.getId())).withRel("eliminar"));
        } catch (Exception e) {
            log.error("Error building links: {}", e.getMessage());
        }
        return Mono.just(item);
    }

    @PostMapping("/lote")
    @Operation(summary = "Agregar lote de ítems al carrito", description = "Agrega múltiples ítems al carrito de compras validando la existencia de los productos y de los usuarios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lote de ítems agregado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos")
    })
    public Mono<ResponseEntity<List<Inventario_cliente>>> agregarItems(@Valid @RequestBody List<Inventario_cliente> items){
        log.info("Agregando lote de {} ítems al carrito", items.size());
        return Flux.fromIterable(items)
                .flatMap(service::RegistrarItem)
                .flatMap(this::agregarLinks)
                .collectList()
                .map(resultado -> ResponseEntity.status(HttpStatus.CREATED).body(resultado));
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los ítems",
            description = "Retorna una lista con todos los ítems de carrito registrados."
    )
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    public ResponseEntity<List<Inventario_cliente>> listar() {

        log.info("Listando todos los ítems del carrito");

        List<Inventario_cliente> items = service.listar();

        items.forEach(item -> {
            item.add(
                    linkTo(methodOn(InventarioCliController.class)
                            .listar())
                            .withSelfRel()
            );

            item.add(
                    linkTo(methodOn(InventarioCliController.class)
                            .verCarrito(item.getIdUsuario()))
                            .withRel("carrito-usuario")
            );
        });

        return ResponseEntity.ok(items);
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(
            summary = "Ver carrito de un usuario",
            description = "Retorna todos los ítems del carrito pertenecientes a un usuario."
    )
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    public ResponseEntity<List<Inventario_cliente>> verCarrito(
            @PathVariable Long idUsuario) {

        log.info("Obteniendo el carrito para el usuario con ID: {}", idUsuario);

        List<Inventario_cliente> items =
                service.obtenerCarritoPorUsuario(idUsuario);

        items.forEach(item -> {
            item.add(
                    linkTo(methodOn(InventarioCliController.class)
                            .verCarrito(idUsuario))
                            .withSelfRel()
            );

            item.add(
                    linkTo(methodOn(InventarioCliController.class)
                            .listar())
                            .withRel("todos")
            );
        });

        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un ítem del carrito", description = "Elimina del carrito el ítem especificado por el ID.")
    @ApiResponse(responseCode = "244", description = "Ítem eliminado correctamente (sin contenido)")
    public Mono<ResponseEntity<Void>> eliminarItem(@PathVariable Long id){
        log.info("Eliminando ítem del carrito con ID: {}", id);
        return Mono.fromRunnable(() -> service.eliminarDelCarrito(id))
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @DeleteMapping("/usuario/{idUsuario}")
    @Operation(summary = "Vaciar el carrito de un usuario", description = "Elimina todos los ítems de carrito para el usuario especificado.")
    @ApiResponse(responseCode = "204", description = "Carrito vaciado correctamente (sin contenido)")
    public Mono<ResponseEntity<Void>> vaciarCarrito(@PathVariable Long idUsuario){
        log.info("Vaciando el carrito para el usuario con ID: {}", idUsuario);
        return Mono.fromRunnable(() -> service.vaciarCarritoPorUsuario(idUsuario))
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
