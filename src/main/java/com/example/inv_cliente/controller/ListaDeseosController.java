package com.example.inv_cliente.controller;


import com.example.inv_cliente.client.ProductoClient;
import com.example.inv_cliente.client.UsuarioClient;
import com.example.inv_cliente.model.ListaDeseados;
import com.example.inv_cliente.model.Producto;
import com.example.inv_cliente.service.ListaDeseosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@Validated
public class ListaDeseosController {

    private final ProductoClient productoClient;

    @Autowired
    private final ListaDeseosService service;
    @Autowired
    private final UsuarioClient usuarioClient;

    @PostMapping("/agregar")
    public Mono<ResponseEntity<List<ListaDeseados>>> agregarItems(@Valid @RequestBody List<ListaDeseados> items){
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
                ).collectList()
                .map(resultado -> ResponseEntity.status(HttpStatus.CREATED).body(resultado));
    }

    @GetMapping
    public ResponseEntity<List<ListaDeseados>> listar(){
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ListaDeseados>> verLista(@PathVariable Long idUsuario){
        return ResponseEntity.ok(service.obtenerListaPorUsuario(idUsuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id){
        service.eliminarDeLista(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario/{idUsuario}")
    public ResponseEntity<Void> vaciarLista(@PathVariable Long idUsuario){
        service.vaciarListaPorUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
