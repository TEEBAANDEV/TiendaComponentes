package com.example.reportes.servicio;

import com.example.reportes.model.Reporte;
import com.example.reportes.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ReporteService {
    private final ReporteRepository repository;

    public ReporteService(ReporteRepository repository) {
        this.repository = repository;
    }

    public Flux<Reporte> findAll() {
        return Mono.fromCallable(repository::findAll)
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Reporte> findById(Long id) {
        return Mono.fromCallable(() -> repository.findById(id).orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Reporte> save(Reporte reporte) {
        return Mono.fromCallable(() -> repository.save(reporte))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteById(Long id) {
        return Mono.fromRunnable(() -> repository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
