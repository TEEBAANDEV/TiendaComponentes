package com.example.analitica.controller;

import com.example.analitica.client.ProductoClient;
import com.example.analitica.client.UsuarioClient;
import com.example.analitica.model.ProductoDTO;
import com.example.analitica.model.Resena;
import com.example.analitica.repository.Resenarepository;
import com.example.analitica.servicio.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@RestController
@RequestMapping("/api/v1/resenas")
@SecurityRequirement(name = "brearerAuth")
public class ResenaController {

    private final ResenaService resenaService;
    private final ProductoClient productoClient;
    private final UsuarioClient usuarioClient;


    public ResenaController(ResenaService resenaService, ProductoClient productoClient, UsuarioClient usuarioClient) {
        this.resenaService = resenaService;
        this.productoClient = productoClient;
        this.usuarioClient = usuarioClient;
    }

    @GetMapping("/{productoId}/promedio")
    public Mono<Map<String, Object>> getPromedio(@PathVariable Long productoId) {
        return resenaService.obtenerSoloPromedio(productoId);
    }

    @GetMapping
    public ResponseEntity<List<Resena>> obtenerComentarios(){
        return ResponseEntity.ok(resenaService.obtenerComentarios());
    }

    @Operation(
            summary = "Crear reseña",
            description = "Crea nueva reseña en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Reseña creada correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos invalidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Sin permisos"
            )
    })
    @PostMapping("/comentar/{productoId}")
    public Mono<ResponseEntity<Resena>> crearResena(@Valid @RequestBody Resena item){
        return Mono.zip(
                        productoClient.obtenerProducto(item.getProductoId()),
                        usuarioClient.obtenerUsuario(item.getUsuarioId())
                ).flatMap(tuple2 -> {
                        Resena resena = new Resena();
                        resena.setProductoId(item.getProductoId());
                        resena.setUsuarioId(item.getUsuarioId());
                        resena.setComentario(item.getComentario());
                        resena.setCalificacion(item.getCalificacion());
                    return Mono.just(resenaService.crearResena(resena));
                })
                        .map(resena -> ResponseEntity.status(HttpStatus.CREATED).body(resena));
    }
}
