package com.example.analitica.controller;

import com.example.analitica.model.EventoAnalitica;
import com.example.analitica.servicio.AnaliticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/analitica")
@RequiredArgsConstructor
public class AnaliticaController {

    private final AnaliticaService service;

    public AnaliticaController(AnaliticaService service) {
        this.service = service;
    }

    // Listar todos los eventos
    @GetMapping
    public Flux<EventoAnalitica> listarTodos() {
        return service.findAll();
    }

    // Filtrar por tipo de evento
    @GetMapping("/tipo/{tipo}")
    public Flux<EventoAnalitica> filtrarPorTipo(@PathVariable String tipo) {
        return service.findByTipo(tipo);
    }

    // Contar eventos por tipo
    @GetMapping("/tipo/{tipo}/count")
    public Mono<Map<String, Object>> contarPorTipo(@PathVariable String tipo) {
        return service.contarPorTipo(tipo)
                .map(count -> Map.of("tipo", tipo, "total", count));
    }

    // Registrar nuevo evento (desde reportes u otros servicios)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EventoAnalitica> crearEvento(@RequestBody EventoAnalitica evento) {
        return service.save(evento);
    }

    // Obtener estadísticas de ventas
    @GetMapping("/estadisticas/ventas")
    public Mono<Map<String, Object>> getEstadisticasVentas() {
        Flux<EventoAnalitica> eventos = service.findByTipo("COMPRA_REALIZADA");

        return eventos.collectList()
                .map(lista -> {
                    long total = lista.size();
                    double suma = lista.stream().mapToDouble(EventoAnalitica::getValor).sum();
                    double promedio = lista.stream().mapToDouble(EventoAnalitica::getValor).average().orElse(0.0);
                    double max = lista.stream().mapToDouble(EventoAnalitica::getValor).max().orElse(0.0);

                    return Map.of(
                            "total_compras", total,
                            "ventas_totales", suma,
                            "promedio_compra", promedio,
                            "compra_maxima", max
                    );
                });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminarEvento(@PathVariable Long id) {
        return service.deleteById(id);
    }
}
