package com.example.analitica.servicio;

import com.example.analitica.model.EventoAnalitica;
import com.example.analitica.repository.EventoAnaliticaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class AnaliticaService {
    private final EventoAnaliticaRepository repository;

    public AnaliticaService(EventoAnaliticaRepository repository) {
        this.repository = repository;
    }


    public Flux<EventoAnalitica> findAll() {
        return Mono.fromCallable(repository::findAll)
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<EventoAnalitica> findByTipo(String tipoEvento) {
        return Mono.fromCallable(() -> repository.findByTipoEvento(tipoEvento))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Long> contarPorTipo(String tipoEvento) {
        return Mono.fromCallable(() -> repository.countByTipoEvento(tipoEvento))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<EventoAnalitica> save(EventoAnalitica evento) {
        return Mono.fromCallable(() -> repository.save(evento))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteById(Long id) {
        return Mono.fromRunnable(() -> repository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
