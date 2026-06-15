package com.example.reportes.controller;

import com.example.reportes.client.ReciboClient;
import com.example.reportes.model.Reporte;
import com.example.reportes.servicio.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder; // Única importación explícita para evitar ambigüedades
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Controlador reactivo para la gestión y generación automatizada de informes")
@SecurityRequirement(name = "bearerAuth")
public class ReporteController {


    private final ReporteService service;
    private final ReciboClient reciboClient;

    @Operation(
            summary = "Listar todos los reportes",
            description = "Retorna un flujo reactivo con todos los reportes almacenados en el sistema mapeados con HATEOAS."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reportes obtenida correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT inválido o faltante"),
            @ApiResponse(responseCode = "403", description = "Prohibido - No posees los roles requeridos")
    })
    @GetMapping
    public Mono<ResponseEntity<CollectionModel<EntityModel<Reporte>>>> listar(){
        return service.findAll()
                .flatMap(reporte -> WebFluxLinkBuilder.linkTo(
                        WebFluxLinkBuilder.methodOn(ReporteController.class).obtener(reporte.getId())
                ).withSelfRel().toMono().map(link -> EntityModel.of(reporte, link)))
                .collectList()
                .flatMap(listaConLinks -> WebFluxLinkBuilder.linkTo(
                        WebFluxLinkBuilder.methodOn(ReporteController.class).listar()
                ).withSelfRel().toMono().map(selfLink -> ResponseEntity.ok(CollectionModel.of(listaConLinks, selfLink))));
    }

    @Operation(
            summary = "Obtener reporte por ID",
            description = "Busca en la base de datos reactiva un reporte específico mediante su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte localizado con éxito"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "El reporte no existe en los registros")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EntityModel<Reporte>>> obtener(@PathVariable Long id){
        return service.findById(id)
                .flatMap(reporte -> {
                    var selfLink = WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(ReporteController.class).obtener(id)).withSelfRel().toMono();
                    var globalLink = WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(ReporteController.class).listar()).withRel("reportes_global").toMono();

                    return Mono.zip(selfLink, globalLink)
                            .map(tuple -> EntityModel.of(reporte, tuple.getT1(), tuple.getT2()));
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Generar reporte desde Recibo",
            description = "Consume de forma asíncrona un microservicio externo para validar el recibo y compilar la metadata del reporte de venta."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reporte compilado y guardado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Identificador de recibo no válido o mal estructurado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "503", description = "Servicio externo de recibos no disponible temporalmente")
    })
    @PostMapping("/generar/{idRecibo}")
    public Mono<ResponseEntity<EntityModel<Reporte>>> crearReporte(@PathVariable Long idRecibo){
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
                    return service.save(nuevoReporte);
                })
                .flatMap(guardado -> WebFluxLinkBuilder.linkTo(
                        WebFluxLinkBuilder.methodOn(ReporteController.class).obtener(guardado.getId())
                ).withSelfRel().toMono().map(link -> ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(guardado, link))))
                .onErrorResume(e -> {
                    System.err.println("Error generando reporte: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).<EntityModel<Reporte>>build());
                });
    }

    @Operation(
            summary = "Eliminar reporte permanentemente",
            description = "Remueve el reporte físico correspondiente al identificador provisto de la base de datos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reporte removido exitosamente del sistema"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Sin privilegios de eliminación"),
            @ApiResponse(responseCode = "404", description = "El reporte no fue encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminar(@PathVariable Long id){
        return service.deleteById(id);
    }
}