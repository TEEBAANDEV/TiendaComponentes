package com.example.recibo.controller;

import com.example.recibo.client.ReciboClient;
import com.example.recibo.model.Recibo;

import com.example.recibo.service.ReciboService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recibo")
@RequiredArgsConstructor

public class ReciboController {
private final ReciboClient reciboClient;

@Autowired
    private final ReciboService service;

@PostMapping
    public Mono<ResponseEntity<Recibo>> crearRecibo(@RequestBody Recibo recibo){
    System.out.println(recibo);

return reciboClient.obtenerRecibo(recibo.getIdRecibo())
        .map(reciboEncontrado -> {
            Recibo guardado = service.save(recibo);
return ResponseEntity.status(HttpStatus.CREATED).body(recibo);
        });}

@GetMapping
public List<Recibo> listarRecibos(){
    return service.listar();
}
}
