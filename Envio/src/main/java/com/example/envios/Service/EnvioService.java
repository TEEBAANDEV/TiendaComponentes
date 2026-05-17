package com.example.envios.Service;


import com.example.envios.modelo.Envio;
import com.example.envios.repository.EnvioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnvioService {

    @Autowired
    private final EnvioRepository repository;

    public Mono<Envio> save(Envio envio) {
        return Mono.fromCallable(() -> repository.save(envio))
                .subscribeOn(Schedulers.boundedElastic());
    }
    public Flux<Envio> listar() {
        return Mono.fromCallable(repository::findAll)
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Envio> findById(Long id) {
        return Mono.fromCallable(() -> repository.findById(id).orElse(null))
                .subscribeOn(Schedulers.boundedElastic());
    }

}
