package com.example.recibo.controller;

import com.example.recibo.client.ReciboClient;
import com.example.recibo.client.VentaClient;
import com.example.recibo.model.Recibo;

import com.example.recibo.service.ReciboService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/recibo")
@RequiredArgsConstructor

public class ReciboController {
    @Autowired
    private final VentaClient ventaClient;

    @Autowired
    private final ReciboService service;

    @PostMapping("/generar/{idVenta}")
    public Mono<ResponseEntity<Recibo>> crearRecibo(@PathVariable Long idVenta){
        return ventaClient.obtenerDetalleVenta(idVenta)
                .publishOn(Schedulers.boundedElastic())
                .map(venta -> {
                    Recibo nuevoRecibo = new Recibo();
                    nuevoRecibo.setIdVenta(venta.getId());
                    nuevoRecibo.setIdUsuario(venta.getIdUsuario());
                    nuevoRecibo.setMontoTotal(venta.getTotal());
                    nuevoRecibo.setMetodoPago("TARJETA");
                    nuevoRecibo.setFechaEmision(venta.getFecha());
                    Recibo guardado = service.save(nuevoRecibo);
                    return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
                })
                .onErrorResume(e -> {
                    System.err.println("Error en recibo: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                });
    }

    @GetMapping
    public List<Recibo> listarRecibos(){
        return service.listar();
    }
}
