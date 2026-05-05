package com.example.ventas.controller;


import com.example.ventas.client.CarritoClient;
import com.example.ventas.client.ProductoClient;
import com.example.ventas.model.Venta;
import com.example.ventas.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api/v1/Ventas")
@RequiredArgsConstructor
public class VentasController {

    private final VentaService ventaService;

    @PostMapping("/comprar/{idUsuario}")
    public Mono<ResponseEntity<Object>> generarCompra(@PathVariable Long idUsuario) {
        return ventaService.procesarVenta(idUsuario)
                .map(ventaGuardada -> ResponseEntity.status(HttpStatus.CREATED).<Object>body(ventaGuardada))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas(){
        return ResponseEntity.ok(ventaService.obtenerVentas());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id){
        ventaService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }


}
