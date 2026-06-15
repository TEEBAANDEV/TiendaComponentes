package com.example.inv_cliente.controller;

import com.example.inv_cliente.client.ProductoClient;
import com.example.inv_cliente.client.UsuarioClient;
import com.example.inv_cliente.model.ListaDeseados;
import com.example.inv_cliente.model.Producto;
import com.example.inv_cliente.service.ListaDeseosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder; // Importamos la clase normal de MVC
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder; // Importamos la clase normal de WebFlux
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@Validated
@Tag(name = "Lista de Deseos", description = "Controlador para gestionar los artículos deseados de los clientes")
@SecurityRequirement(name = "bearerAuth")
public class ListaDeseosController {

    private final ProductoClient productoClient;
    private final ListaDeseosService service;
    private final UsuarioClient usuarioClient;

    @Operation(
            summary = "Agregar múltiples ítems a la Lista de Deseos",
            description = "Proceso híbrido/reactivo que valida externamente el producto y usuario antes de guardar."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ítems agregados exitosamente a la lista"),
            @ApiResponse(responseCode = "400", description = "Cuerpo de la petición o datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Prohibido")
    })
    @PostMapping("/agregar")
    public Mono<ResponseEntity<CollectionModel<EntityModel<ListaDeseados>>>> agregarItems(@Valid @RequestBody List<ListaDeseados> items){
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
                )
                // SOLUCIÓN: Usamos la ruta de clase completa de WebFluxLinkBuilder de manera explícita
                .flatMap(itemGuardado -> WebFluxLinkBuilder.linkTo(
                        WebFluxLinkBuilder.methodOn(ListaDeseosController.class).verLista(itemGuardado.getIdUsuario())
                ).withRel("mi_lista").toMono().map(link -> EntityModel.of(itemGuardado, link)))
                .collectList()
                .flatMap(listaConLinks -> WebFluxLinkBuilder.linkTo(
                        WebFluxLinkBuilder.methodOn(ListaDeseosController.class).listar()
                ).withSelfRel().toMono().map(selfLink -> ResponseEntity.status(HttpStatus.CREATED).body(CollectionModel.of(listaConLinks, selfLink))));
    }

    @Operation(summary = "Listar todos los ítems de deseos", description = "Retorna una colección global de todos los registros síncronos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista global obtenida correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ListaDeseados>>> listar(){
        List<ListaDeseados> resultado = service.listar();

        // SOLUCIÓN: Usamos WebMvcLinkBuilder de forma explícita para el entorno síncrono
        List<EntityModel<ListaDeseados>> conLinks = resultado.stream()
                .map(item -> EntityModel.of(item,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ListaDeseosController.class).verLista(item.getIdUsuario())).withRel("lista_usuario")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(conLinks,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ListaDeseosController.class).listar()).withSelfRel()));
    }

    @Operation(summary = "Ver lista de deseos por Usuario", description = "Filtra y retorna los artículos guardados de un cliente específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista del usuario encontrada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<CollectionModel<EntityModel<ListaDeseados>>> verLista(@PathVariable Long idUsuario){
        List<ListaDeseados> resultado = service.obtenerListaPorUsuario(idUsuario);

        // SOLUCIÓN: Usamos WebMvcLinkBuilder de forma explícita
        List<EntityModel<ListaDeseados>> conLinks = resultado.stream()
                .map(item -> EntityModel.of(item,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ListaDeseosController.class).eliminarItem(item.getId())).withRel("eliminar_item")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(conLinks,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ListaDeseosController.class).verLista(idUsuario)).withSelfRel()));
    }

    @Operation(summary = "Eliminar un ítem de la lista", description = "Borra permanentemente un artículo de la lista de deseos por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artículo eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "El ítem no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id){
        service.eliminarDeLista(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Vaciar lista de un usuario", description = "Borra todos los artículos asociados a un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lista vaciada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @DeleteMapping("/usuario/{idUsuario}")
    public ResponseEntity<Void> vaciarLista(@PathVariable Long idUsuario){
        service.vaciarListaPorUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }
}