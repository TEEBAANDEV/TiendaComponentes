package com.example.reportes.controller;

import com.example.reportes.model.Reporte;
import com.example.reportes.servicio.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {
    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }
    @GetMapping
    public Flux<Reporte> listar(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Reporte> obtener(@PathVariable Long id){
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Reporte> crear(@RequestBody Reporte reporte) {
        return service.save(reporte);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminar(@PathVariable Long id){
        return service.deleteById(id);
    }
}
