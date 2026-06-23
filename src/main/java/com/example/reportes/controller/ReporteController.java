package com.example.reportes.controller;

import com.example.reportes.client.ReciboClient;
import com.example.reportes.model.Reporte;
import com.example.reportes.servicio.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reportes", description = "Endpoints para la gestión de reportes de ventas")
public class ReporteController {
    @Autowired
    private final ReporteService service;
    private final ReciboClient reciboClient;

    private Mono<Reporte> agregarEnlaces(Reporte reporte) {
        try {
            reporte.add(linkTo(methodOn(ReporteController.class).obtener(reporte.getId())).withSelfRel());
            reporte.add(linkTo(methodOn(ReporteController.class).listar()).withRel("reportes"));
        } catch (Exception e) {
            log.error("Error building links: {}", e.getMessage());
        }
        return Mono.just(reporte);
    }

    @GetMapping
    @Operation(summary = "Listar todos los reportes", description = "Retorna la lista de todos los reportes de ventas registrados con HATEOAS")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    public Flux<Reporte> listar(){
        log.info("Listando todos los reportes");
        return service.findAll().flatMap(this::agregarEnlaces);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un reporte por ID", description = "Busca un reporte en el sistema y le asocia enlaces HATEOAS")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte encontrado"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    public Mono<Reporte> obtener(@PathVariable Long id){
        log.info("Buscando reporte con ID: {}", id);
        return service.findById(id).flatMap(this::agregarEnlaces);
    }

    @PostMapping("/generar/{idRecibo}")
    @Operation(summary = "Generar un nuevo reporte", description = "Genera un reporte de venta basado en el ID de un recibo existente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reporte generado correctamente"),
        @ApiResponse(responseCode = "500", description = "Error interno al generar el reporte")
    })
    public Mono<ResponseEntity<Reporte>> crearReporte(@PathVariable Long idRecibo){
        log.info("Generando reporte para el recibo ID: {}", idRecibo);
        return reciboClient.obtenerRecibo(idRecibo)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(recibo -> {
                    Reporte nuevoReporte = Reporte.builder()
                            .idRecibo(recibo.getIdRecibo())
                            .nombre("Reporte de Venta - Recibo #" + recibo.getIdRecibo())
                            .descripcion("Venta del usuario " + recibo.getIdUsuario() + ", Detalle: " + recibo.getNombreProducto())
                            .tipoReporte("Venta_Cliente")
                            .estado("ACTIVO")
                            .build();
                    return service.save(nuevoReporte)
                            .flatMap(this::agregarEnlaces)
                            .map(guardado -> ResponseEntity.status(HttpStatus.CREATED).body(guardado));
                })
                .onErrorResume(e -> {
                    log.error("Error generando reporte para el recibo {}: {}", idRecibo, e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un reporte", description = "Elimina de la base de datos el reporte con el ID provisto")
    @ApiResponse(responseCode = "204", description = "Reporte eliminado con éxito")
    public Mono<Void> eliminar(@PathVariable Long id){
        log.info("Eliminando reporte con ID: {}", id);
        return service.deleteById(id);
    }
}
