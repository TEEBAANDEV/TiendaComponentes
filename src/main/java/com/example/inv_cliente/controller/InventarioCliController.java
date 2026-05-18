package com.example.inv_cliente.controller;


import com.example.inv_cliente.client.ProductoClient;
import com.example.inv_cliente.client.UsuarioClient;
import com.example.inv_cliente.model.Inventario_cliente;
import com.example.inv_cliente.model.Producto;
import com.example.inv_cliente.service.InventarioCliService;
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
@RequestMapping("/api/v1/carrito")
@RequiredArgsConstructor
@Validated
public class InventarioCliController {

    private final ProductoClient productoClient;

    @Autowired
    private final InventarioCliService service;
    @Autowired
    private final UsuarioClient usuarioClient;

    @PostMapping("/lote")
    public Mono<ResponseEntity<List<Inventario_cliente>>> agregarItems(@Valid @RequestBody List<Inventario_cliente> items){
        return Flux.fromIterable(items)
                .flatMap(item ->
                    Mono.zip(
                            productoClient.obtenerProducto(item.getIdProducto()),
                            usuarioClient.obtenerUsuario(item.getIdUsuario())
                    ).flatMap(tuple2 -> {
                        Producto producto = tuple2.getT1();
                        item.setNombreProducto(producto.getNombre());
                        item.setDescripcionProducto(producto.getDescripcion());
                        return Mono.fromCallable(() -> service.agregarAlCarrito(item))
                                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
                    })
                ).collectList()
                .map(resultado -> ResponseEntity.status(HttpStatus.CREATED).body(resultado));
    }

    @GetMapping
    public ResponseEntity<List<Inventario_cliente>> listar(){
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Inventario_cliente>> verCarrito(@PathVariable Long idUsuario){
        return ResponseEntity.ok(service.obtenerCarritoPorUsuario(idUsuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id){
        service.eliminarDelCarrito(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario/{idUsuario}")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable Long idUsuario){
        service.vaciarCarritoPorUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
