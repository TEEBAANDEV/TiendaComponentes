package com.example.inv_cliente.controller;


import com.example.inv_cliente.client.ProductoClient;
import com.example.inv_cliente.client.UsuarioClient;
import com.example.inv_cliente.model.Inventario_cliente;
import com.example.inv_cliente.service.InventarioCliService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carrito")
@RequiredArgsConstructor
public class InventarioCliController {

    private final ProductoClient productoClient;

    @Autowired
    private final InventarioCliService service;
    @Autowired
    private final UsuarioClient usuarioClient;

    @PostMapping
    public Mono<ResponseEntity<Inventario_cliente>> agregarItem(@RequestBody Inventario_cliente item){
        return Mono.zip(productoClient.obtenerProducto(item.getIdProducto()),
                usuarioClient.obtenerUsuario(item.getIdUsuario())
        ).map(tuple2 -> {
            Inventario_cliente guardado = service.agregarAlCarrito(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        });
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
