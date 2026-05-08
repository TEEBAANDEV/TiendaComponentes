package com.example.reportes.controller;

import com.example.reportes.client.ReciboClient;
import com.example.reportes.model.Reporte;
import com.example.reportes.servicio.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {
    @Autowired
    private final ReporteService service;
    private final ReciboClient reciboClient;

    @GetMapping
    public Flux<Reporte> listar(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Reporte> obtener(@PathVariable Long id){
        return service.findById(id);
    }

    @PostMapping("/generar/{idRecibo}")
    public Mono<ResponseEntity<Reporte>> crearReporte(@PathVariable Long idRecibo){
        return reciboClient.obtenerRecibo(idRecibo)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(recibo -> {
                    Reporte nuevoReporte = Reporte.builder()
                            .idRecibo(recibo.getIdRecibo())
                            .nombre("Reporte de Venta - Recibo #" + recibo.getIdRecibo())
                            .descripcion("Venta del usuario " + recibo.getIdUsuario() + ", Detalle: " + recibo.getNombreProducto())
                            .tipoReporte("Venta_Cliente")
                            .estado("ACTIVO")
                            .build();
                    return service.save(nuevoReporte)
                            .map(guardado -> ResponseEntity.status(HttpStatus.CREATED).body(guardado));
                })
                .onErrorResume(e -> {
                    System.out.println("Error generando reporte: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminar(@PathVariable Long id){
        return service.deleteById(id);
    }
}
