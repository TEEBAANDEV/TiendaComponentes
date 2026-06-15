package com.example.componentesv2.controller;

import com.example.componentesv2.model.Producto;
import com.example.componentesv2.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder; // Única importación explícita para evitar colisiones
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Controlador reactivo para la gestión del catálogo de componentes tecnológicos")
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {
    /*
    Ahora me da nostalgia cuando pienso en el pasado
    y no tenia que preocuparme que ResponseEntity funcionara
    */
    private final ProductoService service;

    @Operation(
            summary = "Listar todos los productos",
            description = "Retorna el catálogo completo de productos envuelto en un flujo reactivo con soporte hipermedia HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catálogo de productos obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido")
    })
    @GetMapping
    public Mono<ResponseEntity<CollectionModel<EntityModel<Producto>>>> listar(){
        // Convertimos la lista síncrona del servicio a un flujo reactivo Flux
        return Flux.fromIterable(service.listar())
                .flatMap(producto -> WebFluxLinkBuilder.linkTo(
                        WebFluxLinkBuilder.methodOn(ProductoController.class).findById(producto.getId())
                ).withSelfRel().toMono().map(link -> EntityModel.of(producto, link)))
                .collectList()
                .flatMap(listaConLinks -> WebFluxLinkBuilder.linkTo(
                        WebFluxLinkBuilder.methodOn(ProductoController.class).listar()
                ).withSelfRel().toMono().map(selfLink -> ResponseEntity.ok(CollectionModel.of(listaConLinks, selfLink))));
    }

    @Operation(
            summary = "Buscar producto por ID",
            description = "Obtiene los detalles específicos de un componente tecnológico mediante su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto localizado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "El producto solicitado no existe en el catálogo")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EntityModel<Producto>>> findById(@PathVariable Long id){
        return Mono.justOrEmpty(service.findById(id))
                .flatMap(producto -> {
                    var selfLink = WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(ProductoController.class).findById(id)).withSelfRel().toMono();
                    var globalLink = WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(ProductoController.class).listar()).withRel("productos_global").toMono();

                    return Mono.zip(selfLink, globalLink)
                            .map(tuple -> EntityModel.of(producto, tuple.getT1(), tuple.getT2()));
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Agregar un nuevo producto al catálogo",
            description = "Registra un componente verificando las restricciones físicas del modelo y retorna la entidad con sus enlaces."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado e indexado correctamente en el catálogo"),
            @ApiResponse(responseCode = "400", description = "Payload inválido o violación de restricciones de validación"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public Mono<ResponseEntity<EntityModel<Producto>>> agregarProducto(@Valid @RequestBody Producto producto){
        return Mono.fromCallable(() -> service.agregarProducto(producto))
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic()) // Ejecución segura para procesos JPA
                .flatMap(guardado -> WebFluxLinkBuilder.linkTo(
                        WebFluxLinkBuilder.methodOn(ProductoController.class).findById(guardado.getId())
                ).withSelfRel().toMono().map(link -> ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(guardado, link))));
    }

    @Operation(
            summary = "Eliminar un producto del catálogo",
            description = "Remueve físicamente el registro del producto correspondiente al identificador provisto."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto removido exitosamente (No Content)"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "El identificador del producto no fue localizado")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> eliminarProducto(@PathVariable Long id){
        return Mono.fromRunnable(() -> service.eliminarProducto(id))
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
}