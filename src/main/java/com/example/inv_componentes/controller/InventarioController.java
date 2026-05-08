package com.example.inv_componentes.controller;


import com.example.inv_componentes.client.ProductoClient;
import com.example.inv_componentes.model.Inventario;
import com.example.inv_componentes.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final ProductoClient productoClient;

    @Autowired
    private final InventarioService service;

    @PostMapping
    public Mono<ResponseEntity<Inventario>> crearInventario(@RequestBody Inventario inventario){
        return productoClient.obtenerProducto(inventario.getIdProducto())
                .map(producto -> {
                    inventario.setNombreProducto(producto.getNombre());
                    inventario.setDescripcion(producto.getDescripcion());
                    Inventario guardado = service.save(inventario);
                    return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    @GetMapping
    public Flux<Inventario> listar(){
        List<Inventario> listaInventario = service.listar();
        return Flux.fromIterable(listaInventario)
                .flatMap(item -> productoClient.obtenerProducto(item.getIdProducto())
                        .map(producto -> {
                            item.setNombreProducto(producto.getNombre());
                            item.setDescripcion(producto.getDescripcion());
                            return item;
                        })
                        .defaultIfEmpty(item)
                );
    }

    @PutMapping("/descontar")
    public ResponseEntity<Void> descontarStock(@RequestParam Long idProducto, @RequestParam int cantidad){
        service.descontarStock(idProducto, cantidad);
        return ResponseEntity.ok().build();
    }
}
