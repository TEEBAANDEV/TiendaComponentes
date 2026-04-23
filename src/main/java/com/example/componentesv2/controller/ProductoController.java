package com.example.componentesv2.controller;


import com.example.componentesv2.model.Producto;
import com.example.componentesv2.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    @Autowired
    private ProductoService service;


    @GetMapping
    public ResponseEntity<List<Producto>> listar(){
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public Optional<ResponseEntity<Producto>> findById(@PathVariable Long id){
        return Optional.of(service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()));
    }
}
