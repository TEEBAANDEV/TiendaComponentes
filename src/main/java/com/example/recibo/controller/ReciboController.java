package com.example.recibo.controller;

import com.example.recibo.client.VentaClient;
import com.example.recibo.model.Recibo;
import com.example.recibo.model.VentaDTO;
import com.example.recibo.service.ReciboService;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/recibo")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Recibo Controller", description = "Endpoints para la administracion de los recibos")
public class ReciboController {

    @Autowired
    private final VentaClient ventaClient;

    @Autowired
    private final ReciboService service;

    @Operation(summary = "Generar un nuevo recibo", description = "Genera y guarda un recibo a partir del detalle de una venta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recibo generado exitosamente"),
            @ApiResponse(responseCode = "400", description = "ID de venta con formato erróneo"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada o error al generar recibo"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al procesar la glosa o la persistencia")
    })
    @PostMapping("/generar/{idVenta}")
    public ResponseEntity<Recibo> crearRecibo(@PathVariable Long idVenta) {

        try {
            log.info("Iniciando generación de recibo para venta ID: {}", idVenta);

            VentaDTO venta = ventaClient.obtenerDetalleVenta(idVenta).block();

            if (venta == null) {
                return ResponseEntity.notFound().build();
            }

            Recibo nuevoRecibo = new Recibo();
            nuevoRecibo.setIdVenta(venta.getId());
            nuevoRecibo.setIdUsuario(venta.getIdUsuario());

            String glosaCompleta = venta.getDetalles().stream()
                    .map(d -> d.getCantidad() + "x " + d.getNombreProducto()
                            + " (" + d.getDescripcion() + ")")
                    .collect(Collectors.joining(" | "));

            nuevoRecibo.setNombreProducto(glosaCompleta);
            nuevoRecibo.setMontoTotal(venta.getTotal());
            nuevoRecibo.setMetodoPago("TARJETA");
            nuevoRecibo.setFechaEmision(venta.getFecha());

            Recibo guardado = service.save(nuevoRecibo);

            log.info("Recibo guardado exitosamente con ID: {}", guardado.getIdRecibo());

            guardado.add(
                    linkTo(methodOn(ReciboController.class)
                            .obtenerPorId(guardado.getIdRecibo()))
                            .withSelfRel()
            );

            guardado.add(
                    linkTo(methodOn(ReciboController.class)
                            .listarRecibos())
                            .withRel("recibos")
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);

        } catch (Exception e) {
            log.error("Error al generar recibo para la venta {}: {}", idVenta, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Listar todos los recibos", description = "Retorna una lista reactiva (Flux) de todos los recibos registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "500", description = "Error interno al recuperar los datos globales")

    })
    @GetMapping
    public ResponseEntity<List<Recibo>> listarRecibos() {

        log.info("Listando todos los recibos");

        List<Recibo> recibos = service.listar();

        recibos.forEach(recibo -> {
            recibo.add(
                    linkTo(methodOn(ReciboController.class)
                            .obtenerPorId(recibo.getIdRecibo()))
                            .withSelfRel()
            );
        });

        return ResponseEntity.ok(recibos);
    }

    @Operation(summary = "Obtener un recibo por su ID", description = "Busca un recibo específico mediante su identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recibo encontrado"),
            @ApiResponse(responseCode = "400", description = "El ID del recibo ingresado no es válido"),
            @ApiResponse(responseCode = "404", description = "Recibo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
   })
    @GetMapping("/{idRecibo}")
    public ResponseEntity<Optional<Recibo>> obtenerPorId(@PathVariable Long idRecibo) {

        log.info("Buscando recibo por ID: {}", idRecibo);

        Optional<Recibo> recibo = service.obtenerPorId(idRecibo);

        if (recibo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        recibo.get().add(
                linkTo(methodOn(ReciboController.class)
                        .obtenerPorId(recibo.get().getIdRecibo()))
                        .withSelfRel()
        );

        recibo.get().add(
                linkTo(methodOn(ReciboController.class)
                        .listarRecibos())
                        .withRel("recibos")
        );

        return ResponseEntity.ok(recibo);
    }
}

