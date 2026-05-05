package com.example.envios.controller;

import com.example.envios.Service.EnvioService;
import com.example.envios.client.EnvioClient;
import com.example.envios.modelo.Envio;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/envio")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioClient envioClient;

    @Autowired
    private EnvioService service;

    @PostMapping
    public Mono<ResponseEntity<Envio>> crearEnvio(@RequestBody Envio envio){
        return envioClient.obtenerEnvio(envio.getIdRecibido()).
                map(envioEncontrado -> {
                    Envio guardado = service.save(envio);
                    return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
                });
    }

    @GetMapping
    public List<Envio> listar(){
        return service.listar();
    }
}
