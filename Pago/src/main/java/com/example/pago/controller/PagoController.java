package com.example.pago.controller;

import com.example.pago.model.Pago;
import com.example.pago.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {
    @Autowired
    private PagoService service;

    @GetMapping
    public ResponseEntity<List<Pago>> listar() { return ResponseEntity.ok(service.listar()); }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> findById(@PathVariable Long id){
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());


    }
    @PostMapping
    public ResponseEntity<Pago> agregarPago(@RequestBody Pago pago){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarPago(pago));
    }
    @DeleteMapping
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
