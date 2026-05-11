package com.example.analitica.servicio;

import com.example.analitica.model.Resena;
import com.example.analitica.repository.Resenarepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResenaService {
    @Autowired
    private Resenarepository repository;

    public Mono<Map<String, Object>> obtenerSoloPromedio(Long productoId) {
        return Mono.fromCallable(() -> repository.findByProductoId(productoId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(lista -> {
                    double promedio = lista.stream()
                            .mapToInt(Resena::getCalificacion)
                            .average()
                            .orElse(0.0);
                    return Map.of(
                            "productoId", productoId,
                            "promedio_estrellas", promedio,
                            "total_votos", lista.size()
                    );
                });
    }

    public List<Resena> obtenerComentarios (){
        return repository.findAll();
    }
}
