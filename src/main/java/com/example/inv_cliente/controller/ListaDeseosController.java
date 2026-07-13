package com.example.inv_cliente.controller;

import com.example.inv_cliente.client.ProductoClient;
import com.example.inv_cliente.client.UsuarioClient;
import com.example.inv_cliente.model.ListaDeseados;
import com.example.inv_cliente.model.Producto;
import com.example.inv_cliente.service.ListaDeseosService;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Controlador de Lista de Deseos", description = "Endpoints para gestionar la lista de deseos de los usuarios")
public class ListaDeseosController {

    private final ProductoClient productoClient;

    @Autowired
    private final ListaDeseosService service;
    @Autowired
    private final UsuarioClient usuarioClient;

    private Mono<ListaDeseados> addLinks(ListaDeseados item) {
        try {
            item.add(linkTo(methodOn(ListaDeseosController.class).verLista(item.getIdUsuario())).withSelfRel());
            item.add(linkTo(methodOn(ListaDeseosController.class).eliminarItem(item.getId())).withRel("eliminar"));
            item.add(linkTo(methodOn(ListaDeseosController.class).vaciarLista(item.getIdUsuario())).withRel("vaciar"));
        } catch (Exception e) {
            log.error("Error building links: {}", e.getMessage());
        }
        return Mono.just(item);
    }

    @PostMapping("/agregar")
    @Operation(summary = "Agregar items a la lista de deseos", description = "Permite agregar una lista de items a la lista de deseos de un usuario")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Items creados y agregados exitosamente"),
    @ApiResponse(responseCode = "400", description = "Estructura de entrada o datos de validación incorrectos"),
    @ApiResponse(responseCode = "404", description = "El ID de usuario o de producto no fue encontrado en los microservicios externos"),
    @ApiResponse(responseCode = "500", description = "Error interno al procesar la inserción en la lista")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Flux<ListaDeseados> agregarItems(@Valid @RequestBody List<ListaDeseados> items){
        log.info("Agregando {} items a la lista de deseos", items.size());
        return Flux.fromIterable(items)
                .flatMap(item ->
                        Mono.zip(
                                productoClient.obtenerProducto(item.getIdProducto()),
                                usuarioClient.obtenerUsuario(item.getIdUsuario())
                        ).flatMap(tuple2 -> {
                            Producto producto = tuple2.getT1();
                            item.setNombreProducto(producto.getNombre());
                            item.setDescripcionProducto(producto.getDescripcion());
                            return Mono.fromCallable(() -> service.agregarALista(item))
                                    .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
                        })
                ).flatMap(this::addLinks);
    }

    @GetMapping
    @Operation(summary = "Listar todos los elementos de la lista de deseos", description = "Retorna todos los registros de la lista de deseos globales")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Operación realizada con éxito"),
    @ApiResponse(responseCode = "500", description = "Error interno al recuperar el listado global")
    })
    public Flux<ListaDeseados> listar(){
        log.info("Solicitando listado global de todos los deseos");
        return Mono.fromCallable(() -> service.listar())
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::addLinks);
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Ver lista de deseos por usuario", description = "Retorna todos los items de la lista de deseos correspondientes a un usuario")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Operación realizada con éxito"),
    @ApiResponse(responseCode = "404", description = "El usuario especificado no existe o no posee elementos en su lista"),
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Flux<ListaDeseados> verLista(@PathVariable Long idUsuario){
        log.info("Obteniendo lista de deseos para el usuario: {}", idUsuario);
        return Mono.fromCallable(() -> service.obtenerListaPorUsuario(idUsuario))
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::addLinks);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un item de la lista de deseos", description = "Permite eliminar un único item por su ID")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Item eliminado con éxito"),
    @ApiResponse(responseCode = "404", description = "El ID del item de deseos no fue localizado"),
    @ApiResponse(responseCode = "500", description = "Error interno al eliminar el elemento")
    })
            @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminarItem(@PathVariable Long id){
        log.info("Eliminando item de la lista con ID: {}", id);
        return Mono.fromRunnable(() -> service.eliminarDeLista(id))
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .then();
    }

    @DeleteMapping("/usuario/{idUsuario}")
    @Operation(summary = "Vaciar la lista de deseos de un usuario", description = "Elimina todos los elementos asociados a un ID de usuario")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Lista vaciada con éxito"),
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
    @ApiResponse(responseCode = "500", description = "Error interno al vaciar la lista")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> vaciarLista(@PathVariable Long idUsuario){
        log.info("Vaciando lista de deseos para el usuario: {}", idUsuario);
        return Mono.fromRunnable(() -> service.vaciarListaPorUsuario(idUsuario))
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .then();
    }
}
