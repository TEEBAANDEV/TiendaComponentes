package com.example.analitica.controller;

import com.example.analitica.model.Resena;
import com.example.analitica.repository.Resenarepository;
import com.example.analitica.servicio.ResenaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping("/{productoId}/promedio")
    public Mono<Map<String, Object>> getPromedio(@PathVariable Long productoId) {
        return resenaService.obtenerSoloPromedio(productoId);
    }

    @GetMapping
    public ResponseEntity<List<Resena>> obtenerComentarios(){
        return ResponseEntity.ok(resenaService.obtenerComentarios());
    }
}
